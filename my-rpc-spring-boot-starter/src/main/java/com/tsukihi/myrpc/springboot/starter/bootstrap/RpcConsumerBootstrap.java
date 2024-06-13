package com.tsukihi.myrpc.springboot.starter.bootstrap;

import com.tsukihi.myrpc.proxy.ServiceProxyFactory;
import com.tsukihi.myrpc.springboot.starter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * Rpc 服务消费者启动
 * 和服务提供者启动类的实现方式类似，在 Bean 初始化后，通过反射获取到 Bean 的所有属性，
 * 如果属性包含 @RpcReference 注解，那么就为该属性动态生成代理对象并赋值。
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * Bean初始化后执行，注入服务
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {

        Class<?> beanClass = bean.getClass();
        // 遍历对象的所有属性
        Field[] declaredFields = beanClass.getDeclaredFields();
        // 通过反射机制，为属性动态生成代理对象并赋值
        for(Field field : declaredFields){
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if(rpcReference != null){
                // 为属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if(interfaceClass == void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);

                try{
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                }catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
