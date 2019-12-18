package com.zahngff01.rpc.cluster;

import java.net.InetSocketAddress;

/**
 * @author zhangfeifei
 * @Description 负载，分配ip
 * @create 2019/12/18
 */
public interface Cluster {

    /**
     * 负载均衡方法
     *
     * @param serviceName 服务名称
     * @return
     */
    InetSocketAddress getServerIP(String serviceName);
}
