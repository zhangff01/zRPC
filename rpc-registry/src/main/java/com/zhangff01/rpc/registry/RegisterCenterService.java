package com.zhangff01.rpc.registry;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangfeifei
 * @Description 注册中心
 * @create 2019/12/17
 */
@Slf4j
public class RegisterCenterService {

    public static String x = "";

    /**
     * 通过类名获取服务
     *
     * @param className 类名
     * @return
     */
    public static Class getService(String className) {
        //todo
        return null;
    }

    /**
     * 初始化
     *
     * @param registerHost host
     * @param port         端口号
     */
    public static void init(String registerHost, int port) {
        //todo
    }

    /**
     * 注册服务
     *
     * @param className
     * @param clazz
     */
    public static void register(String className, Class clazz) {
        //todo
    }
}
