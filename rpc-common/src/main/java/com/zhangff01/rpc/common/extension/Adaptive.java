package com.zhangff01.rpc.common.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangfeifei
 * @Description 适配器注解 (一般用来注解方法)
 * 1.在类上加上@Adaptive注解的类,是最为明确的创建对应类型Adaptive类,优先级最高.
 * 2.SPI注解中的value是默认值,通过URL获取不到关于取哪个类作为Adaptive类的话,就使用这个默认值,如果URL中可以获取到,用URL中的.
 * @create 2019/12/26
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Adaptive {

}
