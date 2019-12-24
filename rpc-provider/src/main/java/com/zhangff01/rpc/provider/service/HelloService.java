package com.zhangff01.rpc.provider.service;

/**
 * @author zhangfeifei
 * @Description demo服务
 * @create 2019/12/17
 */
public interface HelloService {

    /**
     * 打招呼
     *
     * @param userName 人名
     * @return
     */
    String sayHello(String userName);
}
