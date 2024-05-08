package com.tsukihi.myrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂(用于创建服务代理对象)
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass 服务类
     * @param <T>
     * @return
     */

    public static <T> T getProxy(Class<T> serviceClass) {
        // 通过 Proxy.newProxyInstance 方法为指定类型创建代理对象。
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
}
