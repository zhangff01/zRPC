package com.zhangff01.rpc.common.extension.factory;

import com.zhangff01.rpc.common.extension.Adaptive;
import com.zhangff01.rpc.common.extension.ExtensionLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangfeifei
 * @Description ${todo}
 * @create 2019/12/29
 */
@Adaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {

    private final List<ExtensionFactory> factories;

    public AdaptiveExtensionFactory() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        List<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        for (String name : loader.getSupportedExtensions()) {
            list.add(loader.getExtension(name));
        }
        factories = Collections.unmodifiableList(list);
    }

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        return null;
    }
}
