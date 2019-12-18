package com.zhangff01.rpc.core.proxy;

import com.zahngff01.rpc.cluster.Cluster;
import com.zahngff01.rpc.cluster.RandomCluster;
import com.zhangff01.rpc.remote.NettyClient;
import com.zhangff01.rpc.remote.model.RpcRequest;
import com.zhangff01.rpc.remote.model.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author zhangfeifei
 * @Description 动态代理处理程序
 * @create 2019/12/18
 */
public class ProxyHandler implements InvocationHandler {

    private Class<?> service;

    //远程调用地址
    private InetSocketAddress remoteAddress;

    private Cluster cluster = new RandomCluster();

    public ProxyHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //准备传输的对象
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(service.getName());
        rpcRequest.setServiceMethod(method.getName());
        rpcRequest.setArguments(args);
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setReturnType(method.getReturnType());
        return this.request(rpcRequest);
    }

    private Object request(RpcRequest rpcRequest) throws ClassNotFoundException {
        //获取需要请求的地址
        remoteAddress = cluster.getServerIP(rpcRequest.getServiceName());
        if (remoteAddress == null) {
            return null;
        }
        Object result;
        RpcResponse rpcResponse = (RpcResponse) NettyClient.send(rpcRequest, remoteAddress);
        result = rpcResponse.getResult();
        return result;
    }
}
