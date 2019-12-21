package com.zhangff01.rpc.registry;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangfeifei
 * @Description 注册中心实例工厂
 * @create 2019/12/19
 */
@Slf4j
public class RegisterCenterServiceFactory {

    private static Map<String, RegisterCenterService> registerCenterServiceMap = new ConcurrentHashMap<>();

    public static RegisterCenterService getRegisterCenterServiceInstance(String registerHost) {
        if (Objects.isNull(registerHost)) {
            return null;
        }
        return registerCenterServiceMap.get(registerHost);
    }

    public static void putRegisterCenterServiceInstance(String registerHost,
                                                        RegisterCenterService registerCenterService) {
        if (Objects.isNull(registerHost) || Objects.isNull(registerCenterService)) {
            return;
        }
        registerCenterServiceMap.put(registerHost, registerCenterService);
        log.info("registerHost:{}注册中心实例缓存成功...", registerHost);
    }
}
