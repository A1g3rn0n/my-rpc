package com.tsukihi.myrpc.registry;

import com.tsukihi.myrpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     * @param serviceMetaInfos
     * @return
     */
    void writeCache(List<ServiceMetaInfo> serviceMetaInfos) {
        this.serviceCache = serviceMetaInfos;
    }

    /**
     * 读缓存
     * @return
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }


    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }
}
