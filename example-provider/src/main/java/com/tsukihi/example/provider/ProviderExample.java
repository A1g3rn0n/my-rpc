package com.tsukihi.example.provider;

import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.registry.LocalRegistry;
import com.tsukihi.myrpc.server.HttpServer;
import com.tsukihi.myrpc.server.VertxHttpServer;

/**
 * 简单的提供者示例
 */
public class ProviderExample {

        public static void main(String[] args) {
            // RPC框架初始化
            RpcApplication.init();

            // 注册服务
            LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

            // 启动web服务
            HttpServer httpServer = new VertxHttpServer();
            httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        }
}
