package com.tsukihi.myrpc.bootstrap;

import com.tsukihi.myrpc.RpcApplication;

/**
 * 服务消费者启动类（初始化）
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        // Rpc框架初始化（配置文件和注册中心）
        RpcApplication.init();
    }
}
