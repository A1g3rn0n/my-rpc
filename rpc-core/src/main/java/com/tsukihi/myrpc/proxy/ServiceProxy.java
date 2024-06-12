package com.tsukihi.myrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.constant.RpcConstant;
import com.tsukihi.myrpc.fault.retry.RetryStrategy;
import com.tsukihi.myrpc.fault.retry.RetryStrategyFactory;
import com.tsukihi.myrpc.fault.tolerant.TolerantStrategy;
import com.tsukihi.myrpc.fault.tolerant.TolerantStrategyFactory;
import com.tsukihi.myrpc.loadbalancer.ConsistentHashLoadBalancer;
import com.tsukihi.myrpc.loadbalancer.HashInputStrategy.HashInputStrategy;
import com.tsukihi.myrpc.loadbalancer.HashInputStrategy.HashInputStrategyFactory;
import com.tsukihi.myrpc.loadbalancer.LeastConnectionsLoadBalancer;
import com.tsukihi.myrpc.loadbalancer.LoadBalancer;
import com.tsukihi.myrpc.loadbalancer.LoadBalancerFactory;
import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.model.RpcResponse;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import com.tsukihi.myrpc.registry.Registry;
import com.tsukihi.myrpc.registry.RegistryFactory;
import com.tsukihi.myrpc.serializer.Serializer;
import com.tsukihi.myrpc.serializer.SerializerFactory;
import com.tsukihi.myrpc.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            // key={{serviceName}:{serviceVersion}} 根据key在注册中心获取消费者所的目标服务提供者List
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();

            // 如果配置文件指定的是一致性hash负载均衡算法
            if(loadBalancer instanceof ConsistentHashLoadBalancer) {
                // 获取hash输入参数
                HashInputStrategy strategy = HashInputStrategyFactory.getInstance(rpcConfig.getHashInputStrategy());
                // 根据不同的策略获取不同的hash输入参数
                requestParams = strategy.getHashInput(rpcRequest);
            }


            log.info("requestParams: {}", requestParams);

            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            log.info("selectedServiceMetaInfo: {}", selectedServiceMetaInfo);


            RpcResponse rpcResponse;
            try {
                // 使用重试机制
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());

                // 基于tcp的rpc请求
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e){
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }


            // 无论服务调用是否成功，都释放服务的连接数
            if (loadBalancer instanceof LeastConnectionsLoadBalancer) {
                ((LeastConnectionsLoadBalancer) loadBalancer).releaseServer(selectedServiceMetaInfo);
            }

            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败");
        }
    }
}
