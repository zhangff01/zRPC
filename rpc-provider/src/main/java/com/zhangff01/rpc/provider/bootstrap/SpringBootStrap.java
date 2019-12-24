package com.zhangff01.rpc.provider.bootstrap;

import com.zhangff01.rpc.provider.service.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author zhangfeifei
 * @Description 服务提供端启动类 通过spring
 * @create 2019/12/17
 */
public class SpringBootStrap {

    public static void main(String[] args) throws IOException {
        //服务暴露
        ClassPathXmlApplicationContext providerContext = new ClassPathXmlApplicationContext(new String[]{"z-rpc-provider.xml"});
        providerContext.start();
        //消费方引用
        ClassPathXmlApplicationContext consumerContext = new ClassPathXmlApplicationContext(new String[]{"z-rpc-consumer.xml"});
        HelloService helloService = consumerContext.getBean(HelloService.class);
        System.out.println(helloService.sayHello("zff"));
    }
}
