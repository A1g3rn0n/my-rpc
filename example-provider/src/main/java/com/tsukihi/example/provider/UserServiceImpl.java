package com.tsukihi.example.provider;


import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
/**
 * 用户服务实现
 */

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
//        user.setName("用户名： " + user.getName());
        System.out.println("用户名称：" + user.getName());
        return user;
    }
}
