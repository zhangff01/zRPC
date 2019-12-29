package com.zhangff01.rpc.common.extension.factory;

import com.zhangff01.rpc.common.extension.SPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangfeifei
 * @Description SpringExtensionFactory
 * @create 2019/12/24
 */
@Slf4j
public class SpringExtensionFactory implements ExtensionFactory {

    private static final Set<ApplicationContext> contexts = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void addApplicationContext(ApplicationContext context) {
        contexts.add(context);
    }

    public static void removeApplicationContext(ApplicationContext context) {
        contexts.remove(context);
    }

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        //SPI should be get from SpiExtensionFactory
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            return null;
        }

        for (ApplicationContext context : contexts) {
            if (context.containsBean(name)) {
                Object bean = context.getBean(name);
                if (type.isInstance(bean)) {
                    return (T) bean;
                }
            }
        }

        log.warn("No spring extension(bean) named:" + name + ", try to find an extension(bean) of type " + type.getName());

        for (ApplicationContext context : contexts) {
            try {
                return context.getBean(type);
            } catch (NoUniqueBeanDefinitionException multiBeanExe) {
                throw multiBeanExe;
            } catch (NoSuchBeanDefinitionException noBeanExe) {
                if (log.isDebugEnabled()) {
                    log.debug("Error when get spring extension(bean) for type:" + type.getName(), noBeanExe);
                }
            }
        }

        log.warn("No spring extension(bean) named:" + name + ", type:" + type.getName() + " found, stop get bean.");
        return null;
    }
}
