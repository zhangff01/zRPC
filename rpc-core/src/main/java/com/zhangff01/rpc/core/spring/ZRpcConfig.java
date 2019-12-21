package com.zhangff01.rpc.core.spring;

import lombok.Data;

/**
 * @author zhangfeifei
 * @Description RPC的配置DTO
 * @create 2019/12/21
 */
@Data
public class ZRpcConfig {

    private Integer port;
    private Integer nThreads;
    private String registerHost;
}
