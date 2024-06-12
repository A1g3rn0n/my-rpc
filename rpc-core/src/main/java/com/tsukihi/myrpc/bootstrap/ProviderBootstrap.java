package com.tsukihi.myrpc.bootstrap;


import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.config.RegistryConfig;
import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import com.tsukihi.myrpc.model.ServiceRegisterInfo;
import com.tsukihi.myrpc.registry.LocalRegistry;
import com.tsukihi.myrpc.registry.Registry;
import com.tsukihi.myrpc.registry.RegistryFactory;
import com.tsukihi.myrpc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {

    /**
     * 初始化
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // Rpc框架初始化
        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList){
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try{
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName + "注册服务失败", e);
            }
        }

        // 启动服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
