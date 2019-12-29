package com.zhangff01.rpc.common.extension.factory;

import com.zhangff01.rpc.common.extension.SPI;

/**
 * @author zhangfeifei
 * @Description 对象工厂
 * @create 2019/12/24
 */
@SPI
public interface ExtensionFactory {

    /**
     * Get extension.
     *
     * @param type object type.
     * @param name object name.
     * @return object instance.
     */
    <T> T getExtension(Class<T> type, String name);
}
