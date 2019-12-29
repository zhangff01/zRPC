package com.zhangff01.rpc.common.extension;

import com.zhangff01.rpc.common.extension.factory.ExtensionFactory;
import com.zhangff01.rpc.common.util.Holder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhangfeifei
 * @Description Load extensions
 * @create 2019/12/24
 */
@Slf4j
public class ExtensionLoader<T> {

    private final Class<?> type;

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private Set<Class<?>> cachedWrapperClasses;

    /**
     * 拓展类名称 缓存拓展类实例
     */
    private Map<String, Class<?>> cachedClasses = new HashMap<>();

    /**
     * 缓存拓展类实例 拓展类名称
     */
    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();

    /**
     * SPI - 拓展类class和实例 map
     */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    /**
     * SPI
     */
    private String cachedDefaultName;

    /**
     * SPI
     */
    private final ConcurrentMap<String, Holder<Object>> cachedSPIValueInstances = new ConcurrentHashMap<>();

    /**
     * Activate
     */
    private final Map<String, Object> cachedActivates = new ConcurrentHashMap<>();

    /**
     * Adaptive
     */
    private final Holder<Object> cachedAdaptiveInstance = new Holder();

    /**
     * Adaptive
     */
    private volatile Class<?> cachedAdaptiveClass = null;

    /**
     * Adaptive
     */
    private volatile Throwable createAdaptiveInstanceError;

    /**
     * 拓展类工厂
     */
    private final ExtensionFactory objectFactory;

    /**
     * 异常map
     */
    private Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        objectFactory = type == ExtensionFactory.class ? null
                : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
    }

    /**
     * 重要方法
     *
     * @param type
     * @return
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type(" + type +
                    ") is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
        }

        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    /**
     * 【基本实现】在ExtensionLoader中获取默认实现类或者通过实现类名称来获取实现类
     *
     * @param spiValue SPI的value值
     * @return
     */
    public T getExtension(String spiValue) {
        if (spiValue == null || spiValue.length() == 0) {
            throw new IllegalArgumentException("Extension spiValue == null");
        }
        if ("true".equals(spiValue)) {
            return getDefaultExtension();
        }
        Holder<Object> holder = cachedSPIValueInstances.get(spiValue);
        if (holder == null) {
            cachedSPIValueInstances.putIfAbsent(spiValue, new Holder<Object>());
            holder = cachedSPIValueInstances.get(spiValue);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(spiValue);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    public T getDefaultExtension() {
        getExtensionClasses();
        if (null == cachedDefaultName || cachedDefaultName.length() == 0 || "true".equals(cachedDefaultName)) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }

    /**
     * 根据spi的Value创建扩展类
     *
     * @param spiValue
     * @return
     */
    private T createExtension(String spiValue) {
        Class<?> clazz = getExtensionClasses().get(spiValue);
        if (clazz == null) {
            throw new IllegalStateException("No such extension " + type.getName() + " by name " + spiValue);
        }
        try {
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            injectExtension(instance);
            Set<Class<?>> wrapperClasses = cachedWrapperClasses;
            if (wrapperClasses != null && !wrapperClasses.isEmpty()) {
                for (Class<?> wrapperClass : wrapperClasses) {
                    instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
                }
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance(name: " + spiValue + ", class: " +
                    type + ")  could not be instantiated: " + t.getMessage(), t);
        }
    }

    /**
     * 拓展类自动注入
     *
     * @param instance
     * @return
     */
    private T injectExtension(T instance) {
        try {
            if (objectFactory == null) {
                return instance;
            }
            //遍历方法形如public setXXX(T t)的方法
            for (Method method : instance.getClass().getMethods()) {
                if (method.getName().startsWith("set") && method.getParameterTypes().length == 1 && Modifier.isPublic(method.getModifiers())) {
                    Class<?> pt = method.getParameterTypes()[0];
                    try {
                        String property = method.getName().length() > 3 ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
                        Object object = objectFactory.getExtension(pt, property);
                        if (object != null) {
                            method.invoke(instance, object);
                        }
                    } catch (Exception e) {
                        log.error("fail to inject via method " + method.getName()
                                + " of interface " + type.getName() + ": " + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return instance;
    }

    /**
     * 重要方法获取适配器类
     *
     * @return
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses;
        if (classes.size() == 0) {
            synchronized (cachedClasses) {
                if (classes.size() == 0) {
                    classes = loadExtensionClasses();
                    cachedClasses = classes;
                }
            }
        }
        return classes;
    }

    /**
     * 加载拓展类
     *
     * @return
     */
    private Map<String, Class<?>> loadExtensionClasses() {
        //判断是否有SPI注解
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value();
            if ((value = value.trim()).length() > 0) {
                String[] names = value.split(",");
                if (names.length > 1) {
                    throw new IllegalStateException("more than 1 default extension name on extension " + type.getName()
                            + ": " + Arrays.toString(names));
                }
                if (names.length == 1) {
                    cachedDefaultName = names[0];
                }
            }
        }
        //根据文件路径加载
        Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
        loadDirectory(extensionClasses, "META-INF/zRPC/internal/", type.getName());
        loadDirectory(extensionClasses, "META-INF/zRPC/", type.getName());
        loadDirectory(extensionClasses, "META-INF/services/", type.getName());
        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type) {
        String fileName = dir + type;
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    java.net.URL resourceURL = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceURL);
                }
            }
        } catch (Throwable t) {
            log.error("Exception when load extension class(interface: " + type + ", description file: " + fileName + ").", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, java.net.URL resourceURL) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), "utf-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (line.length() > 0) {
                                loadClass(extensionClasses, resourceURL, Class.forName(line, true, classLoader), name);
                            }
                        } catch (Throwable t) {
                            IllegalStateException e = new IllegalStateException("Failed to load extension class(interface: "
                                    + type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                            exceptions.put(line, e);
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Throwable t) {
            log.error("Exception when load extension class(interface: " +
                    type + ", class file: " + resourceURL + ") in " + resourceURL, t);
        }
    }

    private void loadClass(Map<String, Class<?>> extensionClasses, java.net.URL resourceURL, Class<?> clazz, String name) throws NoSuchMethodException {
        //判断接口是否有继承关系
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Error when load extension class(interface: " +
                    type + ", class line: " + clazz.getName() + "), class "
                    + clazz.getName() + "is not subtype of interface.");
        }
        //是否有Adaptive注解
        if (clazz.isAnnotationPresent(Adaptive.class)) {
            if (cachedAdaptiveClass == null) {
                cachedAdaptiveClass = clazz;
            } else if (!cachedAdaptiveClass.equals(clazz)) {
                throw new IllegalStateException("More than 1 adaptive class found: "
                        + cachedAdaptiveClass.getClass().getName()
                        + ", " + clazz.getClass().getName());
            }
        }//获取到构造函数则添加包装类
        else if (isWrapperClass(clazz)) {
            Set<Class<?>> wrappers = cachedWrapperClasses;
            if (wrappers == null) {
                cachedWrapperClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
                wrappers = cachedWrapperClasses;
            }
            wrappers.add(clazz);
        } else {
            clazz.getConstructor();
            if (name == null || name.length() == 0) {
                String simpleName = clazz.getSimpleName();
                if (simpleName.endsWith(type.getSimpleName())) {
                    simpleName = simpleName.substring(0, simpleName.length() - type.getSimpleName().length());
                }
                name = simpleName.toLowerCase();
                if (name.length() == 0) {
                    throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + resourceURL);
                }
            }
            String[] names = name.split(",");
            if (names != null && names.length > 0) {
                Activate activate = clazz.getAnnotation(Activate.class);
                if (activate != null) {
                    cachedActivates.put(names[0], activate);
                }
                for (String n : names) {
                    if (!cachedNames.containsKey(clazz)) {
                        cachedNames.put(clazz, n);
                    }
                    Class<?> c = extensionClasses.get(n);
                    if (c == null) {
                        extensionClasses.put(n, clazz);
                    } else if (c != clazz) {
                        throw new IllegalStateException("Duplicate extension " + type.getName() + " name " + n + " on " + c.getName() + " and " + clazz.getName());
                    }
                }
            }
        }
    }

    /**
     * 获取到构造函数则添加包装类
     *
     * @param clazz
     * @return
     */
    private boolean isWrapperClass(Class<?> clazz) {
        try {
            clazz.getConstructor(type);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static ClassLoader findClassLoader() {
        return ExtensionLoader.class.getClassLoader();
    }

    //---- 下面是Adaptive注解逻辑 ----//

    /**
     * 单例模式 获取代理类
     *
     * @return
     */
    public T getAdaptiveExtension() {
        //首先尝试从缓存中获取实例，如果不存在则创建一个新的实例
        Object instance = cachedAdaptiveInstance.get();
        if (instance != null) {
            return (T) instance;
        }
        if (createAdaptiveInstanceError == null) {
            //使用DCL(double-check-locking)模式进行单例实例化
            synchronized (cachedAdaptiveInstance) {
                instance = cachedAdaptiveInstance.get();
                if (instance == null) {
                    try {
                        instance = createAdaptiveExtension();
                        cachedAdaptiveInstance.set(instance);
                    } catch (Throwable t) {
                        createAdaptiveInstanceError = t;
                        throw new IllegalStateException("fail to create adaptive instance: " + t.toString(), t);
                    }
                }
            }
        } else {
            throw new IllegalStateException("fail to create adaptive instance: " + createAdaptiveInstanceError.toString(), createAdaptiveInstanceError);
        }
        return (T) instance;
    }

    private T createAdaptiveExtension() {
        try {
            //获取AdaptiveExtensionClass并完成注入
            //基本分两步：1.获取适配器类 2.在适配器里面注入其他的扩展点
            return injectExtension((T) getAdaptiveExtensionClass().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Can not create adaptive extension " + type + ", cause: " + e.getMessage(), e);
        }
    }

    /**
     * 获得适配类有两种途径，第一就是某个实现类上被@Adaptive注解，
     * 第二就是没有实现类被注解，因此会自动生成一个某个接口的适配类 这里只模仿第一种情况的
     **/
    private Class<?> getAdaptiveExtensionClass() {
        getExtensionClasses();
        if (cachedAdaptiveClass != null) {
            return cachedAdaptiveClass;
        }
        return null;
    }

    public Set<String> getSupportedExtensions() {
        Map<String, Class<?>> clazzes = getExtensionClasses();
        return Collections.unmodifiableSet(new TreeSet<String>(clazzes.keySet()));
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[" + type.getName() + "]";
    }
}
