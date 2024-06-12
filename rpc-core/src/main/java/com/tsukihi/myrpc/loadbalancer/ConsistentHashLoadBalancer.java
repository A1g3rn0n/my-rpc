package com.tsukihi.myrpc.loadbalancer;

import com.tsukihi.myrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器，消费者请求的hash值是根据消费者ip地址生成的，也就是说来自同一个服务消费者的请求会始终被分配到同一个服务提供者。
 * 在需要保持会话一致性的场景中，一致性哈希也是一个很好的选择。
 * 例如，如果你有一个在线游戏的服务器集群，你可能希望同一个玩家的所有请求都被路由到同一个服务器，
 * 这样可以保持玩家的会话状态。在这种情况下，你可以使用玩家的ID或者IP地址作为一致性哈希的输入，
 * 这样同一个玩家的所有请求都会被路由到同一个服务器。
 *
 * 曾纠结过在rpc的注册中心，也就是消费者负载均衡选择生产者，
 * ip hash时，使用消费者ip还是用户的ip，在需要会话一致性的场景中其实都可以，使用消费者ip更容易实现
 * 会话一致性。需要用户选择消费者（消费者可能也会有自己的服务）这里也是需要负载均衡，同一个用户的请求指向了同一个消费者
 * 消费者调用生产者，使用消费者ip作为hash因子，负载均衡选择生产者，保证同一个消费者的请求指向同一个生产者
 * 至此，同一用户的请求指向了同一个生产者，实现了会话一致性
 *
 * 使用 TreeMap 实现一致性 Hash 环，
 * 该数据结构提供了 ceilingEntry 和 firstEntry 两个方法，便于获取符合算法要求的节点。
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{

    /**
     * 一致性Hash环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_SIZE = 100;


    /**
     * 选择服务调用
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 服务列表
     * @return
     */

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }

        // 构建虚拟节点环
        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList){
            for(int i = 0; i < VIRTUAL_NODE_SIZE; i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取调用请求的hash值
        int hash = getHash(requestParams);

        // 选择最接近并且大于等于调用请求hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null){
            // 如果没有大于等于调用请求hash值的虚拟节点，则返回环首部的节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    @Override
    public void releaseServer(ServiceMetaInfo serviceMetaInfo) {
        // do nothing
    }

    /**
     * Hash算法，可自行实现
     */
    private int getHash(Object key){
        return key.hashCode();
    }
}
