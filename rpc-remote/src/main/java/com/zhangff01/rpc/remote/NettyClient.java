package com.zhangff01.rpc.remote;

import com.zhangff01.rpc.remote.handler.MyDecoder;
import com.zhangff01.rpc.remote.handler.MyEncoder;
import com.zhangff01.rpc.remote.handler.NettyClientHandler;
import com.zhangff01.rpc.remote.model.RpcRequest;
import com.zhangff01.rpc.remote.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author zhangfeifei
 * @Description netty实现的客户端
 * @create 2019/12/18
 */
public class NettyClient {

    private static Integer TIMEOUT = 1000;

    public static RpcResponse send(RpcRequest rpcRequest, InetSocketAddress inetSocketAddress) {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            ch.pipeline().addLast(new MyEncoder(RpcRequest.class));
                            ch.pipeline().addLast(new MyDecoder(RpcResponse.class));
                            ch.pipeline().addLast(nettyClientHandler);
                        }
                    });

            ChannelFuture future = bootstrap.connect(inetSocketAddress.getAddress(), inetSocketAddress.getPort()).sync();
            future.channel().writeAndFlush(rpcRequest);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return nettyClientHandler.getResult();
    }
}
