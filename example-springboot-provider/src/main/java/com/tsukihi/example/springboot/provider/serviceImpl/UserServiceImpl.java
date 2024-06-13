package com.tsukihi.example.springboot.provider.serviceImpl;

import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("这里是注解测试 用户名： " + user.getName());
        return user;
    }

    @Override
    public short getNumber() {
        return 6;
    }
}
