package com.zhangff01.rpc.registry.impl;

import com.zhangff01.rpc.registry.DiscoverService;
import com.zhangff01.rpc.registry.RegisterCenterService;
import lombok.Data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangfeifei
 * @Description Jvm实现的注册中心
 * @create 2019/12/18
 */
@Data
public class JvmRegisterCenter implements RegisterCenterService {

    /**
     * 注册中心地址
     */
    private static String registerHost;

    private static String localIp;

    /**
     * 暴露接口的实现类存放容器
     */
    private static ConcurrentHashMap<String, Class> registerServices = new ConcurrentHashMap<>();

    /**
     * 暴露接口到注册中心 key格式: serviceName val: ip:port
     */
    private static ConcurrentHashMap<String, String> registerCenterMap = new ConcurrentHashMap<>();

    @Override
    public Class getService(String className) {
        return registerServices.get(className);
    }

    @Override
    public void init(String registerHost, int port) {
        try {
            DiscoverService.registerCenterService = this;
            //提供层的ip,这里存放本机的ip
            localIp = InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String className, Class clazz) {
        registerServices.put(className, clazz);
        String ips = registerCenterMap.get(className);
        if (ips == null) {
            ips = localIp;
        } else {
            ips += ";" + localIp;
        }
        registerCenterMap.put(className, ips);
    }

    @Override
    public String getServiceIps(String className) {
        return registerCenterMap.get(className);
    }
}
