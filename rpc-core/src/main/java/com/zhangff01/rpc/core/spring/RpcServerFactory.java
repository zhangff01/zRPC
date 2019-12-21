package com.zhangff01.rpc.core.spring;

import com.zhangff01.rpc.core.RpcServer;
import com.zhangff01.rpc.core.RpcServerImpl;
import lombok.Data;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * @author zhangfeifei
 * @Description 用户读取xml中的接口
 * @create 2019/12/21
 */
@Data
public class RpcServerFactory implements InitializingBean, DisposableBean {

    /**
     * rpc配置
     */
    private ZRpcConfig zRpcConfig;

    /**
     * 要暴露的服务map
     */
    private Map<String, Class> services;

    /**
     * 核心服务
     */
    private RpcServer rpcServer;

    @Override
    public void destroy() throws Exception {
        rpcServer.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.rpcServer = new RpcServerImpl(zRpcConfig.getPort(), zRpcConfig.getNThreads(), zRpcConfig.getRegisterHost(), false);
        for (String key : services.keySet()) {
            try {
                rpcServer.register(key, services.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //启动rpc服务
        this.rpcServer.start();
    }
}
