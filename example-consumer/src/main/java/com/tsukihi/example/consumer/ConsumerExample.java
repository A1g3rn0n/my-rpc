package com.tsukihi.example.consumer;

import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.proxy.ServiceProxyFactory;
import com.tsukihi.myrpc.utils.ConfigUtils;

import java.lang.reflect.Method;

/**
 * 简单的消费者示例
 */
public class ConsumerExample {

    public static void main(String[] args) {
        /**
         * 测试mock
         */
        // 获取代理

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Algernon");


        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.printf("ConsumerExample.java OutPut user.getName(): {%s}", newUser.getName());
            System.out.println();
        } else {
            System.out.println("user == null");
        }

        // todo short类型方法 返回值赋值给short型变量 为什么会发生类型转换错误。
        //  long也会 double也会 但是方法返回类型和变量类型都是int就不会
//        short number = userService.getNumber();
//        System.out.println(number);
    }
}
