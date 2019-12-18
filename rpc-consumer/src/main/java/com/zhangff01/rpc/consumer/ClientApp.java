package com.zhangff01.rpc.consumer;

import com.zhangf01.rpc.provider.service.HelloService;
import com.zhangff01.rpc.core.proxy.RemoteServiceProxy;

/**
 * @author zhangfeifei
 * @Description 服务调用（客户端）
 * @create 2019/12/18
 */
public class ClientApp {

    public static void main(String[] args) {
        //设置registry地址
        RemoteServiceProxy.setRegistryAddress("127.0.0.1:2181");
        //获取远程服务
        HelloService helloService = RemoteServiceProxy.newRemoteProxyObject(HelloService.class);
        String result = helloService.sayHello("zff");
        System.out.println(result);
    }
}
