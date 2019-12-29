package com.zhangff01.rpc.common.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangfeifei
 * @Description ${todo}
 * @create 2019/12/26
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Adaptive {

}
