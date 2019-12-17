package com.zhangf01.rpc.provider.service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangfeifei
 * @Description 服务实现类
 * @create 2019/12/17
 */
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String userName) {
        String str = userName + " hello!";
        log.info(str);
        return str;
    }
}
