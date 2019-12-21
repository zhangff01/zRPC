package com.zhangf01.rpc.provider.bootstrap;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author zhangfeifei
 * @Description 服务提供端启动类 通过spring
 * @create 2019/12/17
 */
public class SpringBootStrap {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"z-rpc-provider.xml"});
        context.start();
        System.in.read(); // 按任意键退出
    }
}
