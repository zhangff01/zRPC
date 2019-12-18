package com.zhangff01.rpc.remote.handler;

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

    private Object result;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("rpc框架服务端处理异常:");
        cause.printStackTrace();
    }
}
