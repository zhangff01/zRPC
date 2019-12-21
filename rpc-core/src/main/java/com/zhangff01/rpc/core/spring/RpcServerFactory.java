package com.zhangff01.rpc.core.spring;

import lombok.Data;

import java.util.Map;

/**
 * @author zhangfeifei
 * @Description ${todo}
 * @create 2019/12/21
 */
@Data
public class RpcServerFactory {

    private ZRpcConfig zRpcConfig;

    private Map<String, Class> services;
}
