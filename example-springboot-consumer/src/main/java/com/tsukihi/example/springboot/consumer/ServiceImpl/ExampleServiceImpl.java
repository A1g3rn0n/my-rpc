package com.tsukihi.example.springboot.consumer.ServiceImpl;

import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * 示例服务实现 调用远程服务 测试注解用
 */
@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test(){
        User user = new User();
        user.setName("Sicca");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
