package com.zahngff01.rpc.cluster;

import com.zhangff01.rpc.registry.DiscoverService;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * @author zhangfeifei
 * @Description 负载均衡 - 随机分配
 * @create 2019/12/18
 */
public class RandomCluster implements Cluster {

    @Override
    public InetSocketAddress getServerIP(String serviceName) {
        List<InetSocketAddress> inetSocketAddressList = DiscoverService.discoverServices(serviceName);
        int length = inetSocketAddressList.size();
        return inetSocketAddressList.get(new Random().nextInt(length));
    }
}
