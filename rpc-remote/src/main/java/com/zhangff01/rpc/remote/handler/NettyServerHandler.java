package com.zhangff01.rpc.remote.handler;

import com.zhangff01.rpc.registry.RegisterCenterService;
import com.zhangff01.rpc.registry.RegisterCenterServiceFactory;
import com.zhangff01.rpc.registry.impl.JvmRegisterCenter;
import com.zhangff01.rpc.remote.model.RpcRequest;
import com.zhangff01.rpc.remote.model.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangfeifei
 * @Description 主处理器
 * @create 2019/12/17
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 单例存放实例化之后的服务
     */
    private static Map<String, Object> serviceObjects = new ConcurrentHashMap<>();

    private RegisterCenterService registerCenterService;

    private String registerHost;

    public NettyServerHandler(String registerHost) {
        this.registerHost = registerHost;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        if (registerCenterService == null) {
            registerCenterService = RegisterCenterServiceFactory.getRegisterCenterServiceInstance(registerHost);
        }
        Class serviceClass = registerCenterService.getService(rpcRequest.getServiceName());

        if (serviceClass == null) {
            throw new Exception("没有找到类 " + rpcRequest.getServiceName());
        }
        Method method = serviceClass.getMethod(rpcRequest.getServiceMethod(), rpcRequest.getParameterTypes());
        if (method == null) {
            throw new Exception("没有找到相应方法 " + rpcRequest.getServiceMethod());
        }

        //执行真正要调用的方法。
        Object object = serviceObjects.get(serviceClass.getName());
        if (object == null) {
            object = serviceClass.newInstance();
            serviceObjects.put(serviceClass.getName(), object);
        }
        Object result = method.invoke(serviceClass.newInstance(), rpcRequest.getArguments());
        //返回执行结果给客户端
        RpcResponse rpcResponse = new RpcResponse(result);
        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
        log.info("RPC 服务{}#{}调用成功...", rpcRequest.getServiceName(), rpcRequest.getServiceMethod());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty服务器处理异常,");
        cause.printStackTrace();
        ctx.close();
    }
}
