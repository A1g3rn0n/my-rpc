package com.tsukihi.myrpc.config;

import com.tsukihi.myrpc.serializer.SerializerKeys;
import lombok.Data;
/**
 * Rpc框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "rpc-framework";

    /**
     * 版本
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口
     */
    private int serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
}
