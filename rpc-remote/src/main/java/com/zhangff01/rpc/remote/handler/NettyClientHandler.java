package com.zhangff01.rpc.remote.handler;

import com.zhangff01.rpc.remote.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangfeifei
 * @Description netty客户端handler
 * @create 2019/12/18
 */
@Data
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * RPC 返回来的响应体
     */
    private RpcResponse result;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = (RpcResponse) msg;
        log.info("RPC 服务调用成功...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("rpc框架服务端处理异常:");
        cause.printStackTrace();
    }
}
