package com.tsukihi.example.provider;

import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.server.HttpServer;
import com.tsukihi.myrpc.registry.LocalRegistry;
import com.tsukihi.myrpc.server.VertxHttpServer;

/**
 * 简单服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
