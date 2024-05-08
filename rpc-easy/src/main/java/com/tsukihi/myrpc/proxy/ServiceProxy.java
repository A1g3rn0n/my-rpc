package com.tsukihi.myrpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.model.RpcResponse;
import com.tsukihi.myrpc.serializer.JdkSerializer;
import com.tsukihi.myrpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK动态代理）
 *
 * 动态代理则是在运行时动态生成代理类。
 * 动态代理不需要为每一个被代理的类都写一个对应的代理类，
 * 一个动态代理类就可以代理任何接口的类。这大大提高了程序的灵活性。
 * 在你的项目中，ServiceProxy 和 ServiceProxyFactory
 * 就是实现动态代理的关键组件。
 * ServiceProxyFactory 的 getProxy 方法可以为任何接口
 * 创建一个代理对象，这个代理对象的行为是由 ServiceProxy 对象定义的。
 *
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @ return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try{
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 发送请求
            // todo 注意 这里地址被硬编码了， 需要使用注册中心和服务发现解决
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080/")
                    .body(bodyBytes)
                    .execute()) {
                // 获取结果
                byte[] result = httpResponse.bodyBytes();
                // 反序列化结果
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
