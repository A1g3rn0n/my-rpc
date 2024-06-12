package com.tsukihi.myrpc.config;

import com.tsukihi.myrpc.fault.retry.RetryStrategyKeys;
import com.tsukihi.myrpc.fault.tolerant.TolerantStrategyKeys;
import com.tsukihi.myrpc.loadbalancer.HashInputStrategy.HashInputStrategyKeys;
import com.tsukihi.myrpc.loadbalancer.LoadBalancerKeys;
import com.tsukihi.myrpc.serializer.SerializerKeys;
import lombok.Data;
/**
 * Rpc框架全局配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "rpc-framework";

    /**
     * 版本
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口
     */
    private int serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * Hash输入策略
     */
    private String hashInputStrategy = HashInputStrategyKeys.METHOD_NAME;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
