package com.tsukihi.myrpc.springboot.starter.bootstrap;

import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.server.tcp.VertxTcpServer;
import com.tsukihi.myrpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


/**
 * Rpc 框架启动
 * 需求是在Spring框架初始化时，获取@EnableRpc注解的属性，并初始化Rpc框架
 * 通过实现Spring的ImportBeanDefinitionRegistrar接口，并在registerBeanDefinitions方法中获取到项目的注解和属性
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * Spring框架初始化时执行，初始化Rpc框架
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        // 获取到@EnableRpc注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // Rpc框架初始化 配置和注册中心
        RpcApplication.init();

        // 全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if(needServer){
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        }else{
            log.info("不启动 server");
        }
    }
}
