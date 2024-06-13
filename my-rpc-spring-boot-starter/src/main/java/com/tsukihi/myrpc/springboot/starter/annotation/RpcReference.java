package com.tsukihi.myrpc.springboot.starter.annotation;

import com.tsukihi.myrpc.constant.RpcConstant;
import com.tsukihi.myrpc.fault.retry.RetryStrategyKeys;
import com.tsukihi.myrpc.fault.tolerant.TolerantStrategyKeys;
import com.tsukihi.myrpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 服务消费者注解 用于注入服务
 * 在需要注入服务代理对象的属性上使用，类似于@Resource
 * 需要指定调用服务相关的属性，比如服务接口类（可能存在多个接口）、版本号、负载均衡器、重试策略、是否Mock模拟调用等
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface RpcReference {

    /**
     * 服务接口类
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 版本
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡策略
     */
    String loadBalance() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * 容错策略
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 是否Mock模拟调用
     */
    boolean mock() default false;
}
