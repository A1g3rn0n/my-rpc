package com.tsukihi.myrpc.model;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将连接数添加到 ServiceMetaInfo 类可能并不是最佳选择。
 * 这是因为 ServiceMetaInfo 类主要用于存储服务的元信息，
 * 如服务名称、版本号、主机和端口等。将连接数添加到此类可能会使其变得过于复杂，
 * 并且可能会违反单一职责原则。
 */
@Getter
public class ServiceLoadInfo {

    final private ServiceMetaInfo serviceMetaInfo;
    @Getter
    final private AtomicInteger currentConnections;



    public ServiceLoadInfo(ServiceMetaInfo serviceMetaInfo) {
        this.serviceMetaInfo = serviceMetaInfo;
        this.currentConnections = new AtomicInteger(0);
    }

    public void incrementConnections() {
        this.currentConnections.incrementAndGet();
    }

    public void decrementConnections() {
        this.currentConnections.decrementAndGet();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ServiceLoadInfo that = (ServiceLoadInfo) obj;
        return serviceMetaInfo.equals(that.serviceMetaInfo);
    }
}
