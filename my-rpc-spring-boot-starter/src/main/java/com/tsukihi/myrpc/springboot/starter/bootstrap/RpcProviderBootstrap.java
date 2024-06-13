package com.tsukihi.myrpc.springboot.starter.bootstrap;

import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.config.RegistryConfig;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import com.tsukihi.myrpc.registry.LocalRegistry;
import com.tsukihi.myrpc.registry.Registry;
import com.tsukihi.myrpc.registry.RegistryFactory;
import com.tsukihi.myrpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Rpc 服务提供者启动
 * 获取所有包含@RpcService注解的类，并通过注解的属性和反射机制，获取到要注册的服务信息，完成服务注册
 * 利用Spring的特性监听Bean的加载，实现BeanPostProcessor接口，重写 postProcessAfterInitialization 方法，在bean初始化后执行注册服务等操作
 * 另一种方式是 主动扫描包，指定扫描路径，遍历所有类文件，针对有注解的类，执行自定义操作
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * Bean初始化后执行，注册服务
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取到所有包含@RpcService注解的类
        // 通过注解的属性和反射机制，获取到要注册的服务信息，完成服务注册
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);

        if(rpcService != null){
            // 需要注册的服务 即包含@RpcService注解的类
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if(interfaceClass == void.class){
                interfaceClass = beanClass.getInterfaces()[0];
            }

            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 2. 注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册中心注册
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try{
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName + "服务注册失败", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
