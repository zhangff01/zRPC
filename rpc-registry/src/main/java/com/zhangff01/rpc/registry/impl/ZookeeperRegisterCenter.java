package com.zhangff01.rpc.registry.impl;

import com.zhangff01.rpc.registry.RegisterCenterService;
import com.zhangff01.rpc.registry.RegisterCenterServiceFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangfeifei
 * @Description zk实现的注册中心
 * @create 2019/12/24
 */
@Data
@Slf4j
public class ZookeeperRegisterCenter implements RegisterCenterService {

    private String registerHost;

    private ZooKeeper zooKeeper;

    private static int TIME_OUT = 10000;

    private static String BASE_PATH = "/zRPC";

    private String localIp;

    /**
     * 暴露接口的实现类存放容器
     */
    private static ConcurrentHashMap<String, Class> registerServices = new ConcurrentHashMap<>();

    @Override
    public Class getService(String className) {
        return registerServices.get(className);
    }

    @Override
    public void init(String registerHost, int port) {
        RegisterCenterServiceFactory.putRegisterCenterServiceInstance(registerHost, this);
        this.registerHost = registerHost;
        try {
            zooKeeper = new ZooKeeper(registerHost, TIME_OUT, null);
            this.localIp = InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String className, Class clazz) {
        registerServices.put(className, clazz);
        //存入zookeeper节点
        //一个服务可配置多个ip,这样就可以有多个提供层提供服务,在客户端使用负载均衡策略即可
        try {
            //创建持久的根结点
            if (zooKeeper.exists(BASE_PATH, false) == null) {
                zooKeeper.create(BASE_PATH, "true".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            if (zooKeeper.exists(BASE_PATH + "/" + className, false) == null) {
                zooKeeper.create(BASE_PATH + "/" + className, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
            byte[] ipsBytes = zooKeeper.getData(BASE_PATH + "/" + className, false, null);
            String ips = new String(ipsBytes);
            if (ips == null || ips.equals("")) {
                ips = localIp;
            } else {
                ips += ";" + localIp;
            }
            //把当前服务的ip地址存如zookeeper中,供消费者发现
            zooKeeper.setData(BASE_PATH + "/" + className, ips.getBytes(), -1);
            log.info("服务{}:{}注册完成", className, ips);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getServiceIps(String className) {
        byte[] ipsBytes = new byte[0];
        try {
            ipsBytes = zooKeeper.getData(BASE_PATH + "/" + className, false, null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new String(ipsBytes);
    }
}
