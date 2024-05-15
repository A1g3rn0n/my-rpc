package com.tsukihi.myrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.constant.RpcConstant;
import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.model.RpcResponse;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import com.tsukihi.myrpc.protocol.*;
import com.tsukihi.myrpc.registry.Registry;
import com.tsukihi.myrpc.registry.RegistryFactory;
import com.tsukihi.myrpc.serializer.JdkSerializer;
import com.tsukihi.myrpc.serializer.Serializer;
import com.tsukihi.myrpc.serializer.SerializerFactory;
import com.tsukihi.myrpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK动态代理）
 * 服务消费者通过代理发送请求
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
@Slf4j
public class ServiceProxy implements InvocationHandler {


    /**
     * 调用代理
     *
     * @ return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        log.info("method invoke: {}" , method.getName());

        String serviceName = method.getDeclaringClass().getName();
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try{
            // 从注册中心获取服务提供者地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }

            // 暂时先取第一个
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 发送TCP请求
            // http
//            byte[] bodyBytes = serializer.serialize(rpcRequest);
//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }

            // tcp
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("调用失败");
        }

    }
}
