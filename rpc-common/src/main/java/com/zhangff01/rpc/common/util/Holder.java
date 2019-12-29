package com.zhangff01.rpc.common.util;

/**
 * @author zhangfeifei
 * @Description Helper Class for hold a value
 * @create 2019/12/26
 */
public class Holder<T> {

    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
