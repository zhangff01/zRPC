package com.zhangff01.rpc.registry;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangfeifei
 * @Description 用于从注册中心获取服务列表
 * @create 2019/12/18
 */
@Slf4j
public class DiscoverService {

    private static Map<String, List<InetSocketAddress>> servicesMap = new ConcurrentHashMap<>();

    private static String registerHost;

    public static List<InetSocketAddress> discoverServices(String serviceName) {

        List<InetSocketAddress> results = servicesMap.get(serviceName);
        if (results != null) {
            return results;
        } else {
            results = new ArrayList<>();
        }
        RegisterCenterService registerCenterService = RegisterCenterServiceFactory.getRegisterCenterServiceInstance(registerHost);
        String resIps = registerCenterService.getServiceIps(serviceName);
        if (resIps == null) {
            log.error(serviceName + "服务没有发现...");
        }

        String[] ips = resIps.split(";");
        for (String resIp : ips) {
            if (resIp == null || "".equals(resIp)) {
                continue;
            }
            String ip = (resIp.split(":"))[0];
            Integer port = Integer.valueOf((resIp.split(":"))[1]);
            InetSocketAddress result = new InetSocketAddress(ip, port);
            results.add(result);
        }
        servicesMap.put(serviceName, results);
        return results;
    }

    public static void setRegistryHost(String registerHost) {
        DiscoverService.registerHost = registerHost;
    }
    //todo 优化这里可以增加一个定时器，其扫描服务端的服务是否可用，如果不可用service
}
