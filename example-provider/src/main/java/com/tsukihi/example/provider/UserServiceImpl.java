package com.tsukihi.example.provider;


import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现
 */
@Slf4j
public class UserServiceImpl implements UserService{
    /**
     * 实现公共模块中定义的用户服务接口。
     * 功能是打印用户的名称，并且返回参数中的用户对象。
     *
     * 获取用户
     *
     * @param user
     * @return
     */
    public User getUser(User user){
        log.info(String.format("用户名称：{%s}", user.getName()));
        return user;
    }
}
