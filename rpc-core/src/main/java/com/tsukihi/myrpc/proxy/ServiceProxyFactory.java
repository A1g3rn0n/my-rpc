package com.tsukihi.myrpc.proxy;

import com.tsukihi.myrpc.RpcApplication;

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
        // 如果配置为模拟调用，则返回模拟代理对象
        if(RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        // 通过 Proxy.newProxyInstance 方法为指定类型创建代理对象。
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }

    /**
     * 获取模拟代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }

}
