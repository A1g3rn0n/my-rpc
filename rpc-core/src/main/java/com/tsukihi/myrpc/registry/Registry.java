package com.tsukihi.myrpc.registry;

import com.tsukihi.myrpc.config.RegistryConfig;
import com.tsukihi.myrpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心
 */
public interface Registry {
    /**
     * 初始化（服务端）
     *
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务 服务端
     * @param serviceMetaInfo   服务元信息
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务 服务端
     * @param serviceMetaInfo 服务元信息
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现
     *
     * @param serviceKey 服务key
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测(服务端)
     */
    void heartbeat();

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey 服务节点key
     */
    void watch(String serviceNodeKey);
}
