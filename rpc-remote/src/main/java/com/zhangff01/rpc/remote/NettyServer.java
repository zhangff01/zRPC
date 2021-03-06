package com.zhangff01.rpc.remote;

import com.zhangff01.rpc.remote.handler.MyDecoder;
import com.zhangff01.rpc.remote.handler.MyEncoder;
import com.zhangff01.rpc.remote.handler.NettyServerHandler;
import com.zhangff01.rpc.remote.model.RpcRequest;
import com.zhangff01.rpc.remote.model.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author zhangfeifei
 * @Description netty实现的服务端, 主要用来处理消费者发出的调用请求，然后本地执行
 * @create 2019/12/17
 */
@Slf4j
public class NettyServer {

    private int port;
    private int threadSize = 5;
    private String registerHost;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(int port, int threadSize, String registerHost) {
        this.port = port;
        this.threadSize = threadSize;
        this.registerHost = registerHost;
    }

    public void start() {
        bossGroup = new NioEventLoopGroup(threadSize);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyDecoder(RpcRequest.class));
                            pipeline.addLast(new MyEncoder(RpcResponse.class));
                            pipeline.addLast(new NettyServerHandler(registerHost));
                        }
                    })
                    //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                    //用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("Rpc netty服务器启动成功 " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            shutdown();
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
