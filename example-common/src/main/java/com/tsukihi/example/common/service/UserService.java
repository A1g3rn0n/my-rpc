package com.tsukihi.example.common.service;
import com.tsukihi.example.common.model.User;

/**
 * 用户服务
 */


public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */

    User getUser(User user);

    /**
     * 测试mock专用方法
     * @return
     */
    default short getNumber(){
        return 1;
    }
}
