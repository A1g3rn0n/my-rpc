package com.tsukihi.myrpc.springboot.starter.annotation;

import com.tsukihi.myrpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.tsukihi.myrpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.tsukihi.myrpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于全局表示标识项目需要引入的RPC框架、执行初始化方法
 * 启用RPC注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 是否需要启动server
     */
    boolean needServer() default true;
}
