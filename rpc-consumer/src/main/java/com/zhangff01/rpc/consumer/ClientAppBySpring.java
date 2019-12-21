package com.zhangff01.rpc.consumer;

import com.zhangf01.rpc.provider.service.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhangfeifei
 * @Description
 * @create 2019/12/21
 */
public class ClientAppBySpring {

    public static void main(String[] args) {
        //消费方引用
        ClassPathXmlApplicationContext consumerContext = new ClassPathXmlApplicationContext(new String[]{"z-rpc-consumer.xml"});
        HelloService helloService = consumerContext.getBean(HelloService.class);
        System.out.println(helloService.sayHello("zff"));
    }
}
