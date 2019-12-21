package com.zhangff01.rpc.core.spring;

import com.zhangff01.rpc.core.proxy.RemoteServiceProxy;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author zhangfeifei
 * @Description 服务引用
 * @create 2019/12/21
 */
@Data
public class ZRpcReference implements InitializingBean, DisposableBean, ApplicationContextAware {

    private DefaultListableBeanFactory beanFactory;

    private ZRpcConfig zRpcConfig;

    /**
     * 远程接口
     */
    private Map<String, Class> references;

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RemoteServiceProxy.setRegistryAddress(zRpcConfig.getRegisterHost());
        for (String key : references.keySet()) {
            Class clazz = references.get(key);
            //获取远程服务
            Object object = RemoteServiceProxy.newRemoteProxyObject(clazz);
            //注入Spring容器
            beanFactory.registerSingleton(key, object);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }
}
