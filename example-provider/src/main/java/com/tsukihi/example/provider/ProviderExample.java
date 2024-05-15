package com.tsukihi.example.provider;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.config.RegistryConfig;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import com.tsukihi.myrpc.registry.LocalRegistry;
import com.tsukihi.myrpc.registry.Registry;
import com.tsukihi.myrpc.registry.RegistryFactory;
import com.tsukihi.myrpc.server.HttpServer;
import com.tsukihi.myrpc.server.VertxHttpServer;
import com.tsukihi.myrpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务提供者示例
 */
@Slf4j
public class ProviderExample {

        public static void main(String[] args) {
            // RPC框架初始化
            RpcApplication.init();

            // 注册服务
            String serviceName = UserService.class.getName();
            LocalRegistry.register(serviceName, UserServiceImpl.class);

            // 注册服务到注册中心
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            log.info("serviceMetaInfo: {}", serviceMetaInfo);
            try{
                registry.register(serviceMetaInfo);
                log.info("register service to registry success");
            }catch (Exception e){
                throw new RuntimeException("register service to registry error", e);
            }



            // 启动web服务
//            HttpServer httpServer = new VertxHttpServer();
//            httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

            // 启动TCP服务
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(8080);
        }
}
