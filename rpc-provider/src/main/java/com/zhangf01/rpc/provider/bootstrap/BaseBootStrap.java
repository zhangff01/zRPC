package com.zhangf01.rpc.provider.bootstrap;

import com.zhangf01.rpc.provider.service.HelloService;
import com.zhangf01.rpc.provider.service.HelloServiceImpl;
import com.zhangff01.rpc.core.RpcServer;
import com.zhangff01.rpc.core.RpcServerImpl;

/**
 * @author zhangfeifei
 * @Description 服务提供端启动类
 * @create 2019/12/17
 */
public class BaseBootStrap {

    public static void main(String[] args) throws Exception {

        RpcServer rpcServer = new RpcServerImpl(7878, 5, "127.0.0.1:2181", true);
        //暴露HelloService接口，具体实现为HelloServiceImpl
        rpcServer.register(HelloService.class.getName(), HelloServiceImpl.class);
        //启动
        rpcServer.start();
    }
}
