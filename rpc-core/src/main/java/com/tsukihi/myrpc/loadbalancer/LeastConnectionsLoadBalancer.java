package com.tsukihi.myrpc.loadbalancer;

import com.tsukihi.myrpc.model.ServiceLoadInfo;
import com.tsukihi.myrpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 最小连接数负载均衡器
 * todo 未实现
 */
@Slf4j
public class LeastConnectionsLoadBalancer implements LoadBalancer{

    /**
     * 服务列表
     */
    final private PriorityQueue<ServiceLoadInfo> servers = new PriorityQueue<ServiceLoadInfo>(((o1, o2) -> {
        return o1.getCurrentConnections().get() - o2.getCurrentConnections().get();
    }));

    public LeastConnectionsLoadBalancer() {
    }

    public LeastConnectionsLoadBalancer(List<ServiceLoadInfo> servers) {
        this.servers.addAll(servers);
    }

    /**
     * 选择服务调用
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        // 初始化服务列表

        if(servers.isEmpty()){
            for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList){
                servers.add(new ServiceLoadInfo(serviceMetaInfo));
            }
        }

        // PriorityQueue
        // 选择连接数最少的服务
        ServiceLoadInfo server = servers.poll();
        // 如果存在服务，则增加服务的连接数
        if(server != null){
            server.getCurrentConnections().incrementAndGet();
            log.info("LeastConnectionsLoadBalancer selected server: {}", server.getServiceMetaInfo());
            log.info("LeastConnectionsLoadBalancer selected server currentConnections: {}", server.getCurrentConnections());
            servers.add(server);
            return server.getServiceMetaInfo();
        }else {
            // 如果不存在服务, 返回null

            throw new RuntimeException("No available server");
        }
    }

    /**
     * 释放服务
     * @param serviceMetaInfo 服务元信息
     */
    public void releaseServer(ServiceMetaInfo serviceMetaInfo){

        // 释放PriorityQueue中服务的连接数

        for(ServiceLoadInfo server : servers){
            if(server.getServiceMetaInfo().equals(serviceMetaInfo)){
                // 从队列中移除元素
                servers.remove(server);
                // 修改元素值
                server.getCurrentConnections().decrementAndGet();

                // log
                log.info("LeastConnectionsLoadBalancer release server: {}", server.getServiceMetaInfo());
                log.info("LeastConnectionsLoadBalancer release server currentConnections: {}", server.getCurrentConnections());

                // 将元素添加回队列，重新排序
                servers.add(server);
                break;
            }
        }
    }
}
