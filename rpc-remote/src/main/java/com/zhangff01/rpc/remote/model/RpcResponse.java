package com.zhangff01.rpc.remote.model;

import lombok.Data;

/**
 * @author zhangfeifei
 * @Description 返回dto
 * @create 2019/12/17
 */
@Data
public class RpcResponse {

    private Object result;

    public RpcResponse(Object object) {
        this.result = object;
    }
}
