package com.zhangff01.rpc.provider.bootstrap;

import com.zhangff01.rpc.provider.service.HelloService;
import com.zhangff01.rpc.provider.service.HelloServiceImpl;
import com.zhangff01.rpc.core.RpcServer;
import com.zhangff01.rpc.core.RpcServerImpl;
import com.zhangff01.rpc.core.proxy.RemoteServiceProxy;

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

        //设置registry地址
        RemoteServiceProxy.setRegistryAddress("127.0.0.1:2181");
        //获取远程服务
        HelloService helloService = RemoteServiceProxy.newRemoteProxyObject(HelloService.class);
        String result = helloService.sayHello("zff");
        System.out.println(result);
    }
}
