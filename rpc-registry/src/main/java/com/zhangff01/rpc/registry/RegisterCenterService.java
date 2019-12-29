package com.zhangff01.rpc.registry;

import com.zhangff01.rpc.common.extension.SPI;

/**
 * @author zhangfeifei
 * @Description 注册中心
 * @create 2019/12/17
 */
@SPI("zookeeper")
public interface RegisterCenterService {

    /**
     * 通过类名获取服务
     *
     * @param className 类名
     * @return
     */
    Class getService(String className);

    /**
     * 初始化
     *
     * @param registerHost 注册中心地址
     * @param port         端口号
     */
    void init(String registerHost, int port);

    /**
     * 注册服务
     *
     * @param className 类名
     * @param clazz     实现累
     */
    void register(String className, Class clazz);

    /**
     * 服务提供方IPs
     *
     * @param className 类名
     * @return
     */
    String getServiceIps(String className);
}
