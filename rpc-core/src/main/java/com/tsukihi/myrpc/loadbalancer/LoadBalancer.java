package com.tsukihi.myrpc.loadbalancer;

import com.tsukihi.myrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器，消费端使用
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     *
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return 选择的服务
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);

    void releaseServer(ServiceMetaInfo serviceMetaInfo);

//    void releaseServer(ServiceMetaInfo serviceMetaInfo);
}
