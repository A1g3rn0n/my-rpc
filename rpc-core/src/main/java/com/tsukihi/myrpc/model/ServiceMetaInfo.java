package com.tsukihi.myrpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息 即注册信息
 */
@Data
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务首次注册到注册中心的时间
     */
    private String registerTime;

    /**
     * 最近一次在注册中心续期的时间
     */
    private String latestRenewalTime;

    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = "default";

    /**
     * 获取服务键名
     *
     * @return
     */
    public String getServiceKey(){
        // 后续可扩展服务分组
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);

        return String.format("%s:%s", this.serviceName, this.serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     *
     * @return
     */
    public String getServiceNodeKey(){
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }


    /**
     * 获取完整服务地址
     *
     * @return
     */
    public String getServiceAddress() {
        if(!StrUtil.contains(serviceHost, "http")){
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ServiceMetaInfo that = (ServiceMetaInfo) obj;
        return serviceName.equals(that.serviceName) && serviceVersion.equals(that.serviceVersion) && serviceHost.equals(that.serviceHost) && servicePort.equals(that.servicePort) && serviceGroup.equals(that.serviceGroup);
    }
}
