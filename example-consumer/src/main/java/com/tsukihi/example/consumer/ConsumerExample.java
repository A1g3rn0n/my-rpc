package com.tsukihi.example.consumer;

import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.proxy.ServiceProxyFactory;
import com.tsukihi.myrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 简单的消费者示例
 */
@Slf4j
public class ConsumerExample {

    public static void main(String[] args) {
        /**
         * 测试mock
         */
        // 获取代理

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("ababa");


        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            log.info(String.format("user.getName(): {%s}", newUser.getName()));
        } else {
            log.info(String.format("user.getName(): {%s}", (Object) null));
        }


        short number = userService.getNumber();
        log.info(String.format("number: {%d}", number));

        short number2 = userService.getNumber();
        log.info(String.format("number2: {%d}", number2));
    }
}
