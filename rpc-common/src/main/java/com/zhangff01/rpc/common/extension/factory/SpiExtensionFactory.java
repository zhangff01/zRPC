package com.zhangff01.rpc.common.extension.factory;

import com.zhangff01.rpc.common.extension.ExtensionLoader;
import com.zhangff01.rpc.common.extension.SPI;

/**
 * @author zhangfeifei
 * @Description SpiExtensionFactory
 * @create 2019/12/24
 */

public class SpiExtensionFactory implements ExtensionFactory {

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            if (!loader.getSupportedExtensions().isEmpty()) {
                return loader.getAdaptiveExtension();
            }
        }
        return null;
    }
}
