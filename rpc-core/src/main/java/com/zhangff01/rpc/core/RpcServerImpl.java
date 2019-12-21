package com.zhangff01.rpc.core;

import com.zhangff01.rpc.registry.RegisterCenterService;
import com.zhangff01.rpc.registry.impl.JvmRegisterCenter;
import com.zhangff01.rpc.remote.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangfeifei
 * @Description RPC服务实现
 * @create 2019/12/17
 */
@Slf4j
public class RpcServerImpl implements RpcServer {

    private int nThreads = 10;
    private boolean isAlive = false;
    private int port = 8989;
    private NettyServer nettyServer;
    private final static ExecutorService executor = Executors.newSingleThreadExecutor();
    private RegisterCenterService registerCenterService;
    private String registerHost;

    public RpcServerImpl() {
        init();
    }

    public RpcServerImpl(int port, int nThreads, String registerHost, Boolean isStart) {
        this.port = port;
        this.nThreads = nThreads;
        this.registerHost = registerHost;
        registerCenterService = new JvmRegisterCenter();
        registerCenterService.init(registerHost, port);
        init();
        if (isStart.equals(Boolean.TRUE)) {
            start();
        }
    }

    public void init() {}

    @Override
    public void start() {
        synchronized (this) {
            if (isAlive) {
                return;
            }
        }
        log.info("开始启动Rpc服务 线程数：" + nThreads);
        isAlive = true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                nettyServer = new NettyServer(port, nThreads, registerHost);
                nettyServer.start();
            }
        });
    }

    @Override
    public void stop() {
        isAlive = false;
        nettyServer.shutdown();
    }

    @Override
    public void register(String className, Class clazz) throws Exception {
        registerCenterService.register(className, clazz);
    }

    @Override
    public boolean isAlive() {
        String status = (this.isAlive == true) ? "RPC服务已经启动" : "RPC服务已经关闭";
        log.info(status);
        return this.isAlive;
    }
}
