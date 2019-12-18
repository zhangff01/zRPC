package com.zhangff01.rpc.core.proxy;

import com.zhangff01.rpc.registry.DiscoverService;

import java.lang.reflect.Proxy;

/**
 * @author zhangfeifei
 * @Description 动态代理
 * @create 2019/12/18
 */
public class RemoteServiceProxy {

    /**
     * 动态代理的真实对象的实现
     *
     * @param service
     * @param <T>
     * @return
     */
    public static <T> T newRemoteProxyObject(final Class<?> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new ProxyHandler(service));
    }

    /**
     * 设置Registry的地址，默认为本机2181接口
     *
     * @param registerHost
     */
    public static void setRegistryAddress(String registerHost) {
        DiscoverService.setRegistryHost(registerHost);
    }
}
