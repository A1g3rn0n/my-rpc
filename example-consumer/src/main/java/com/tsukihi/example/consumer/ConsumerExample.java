package com.tsukihi.example.consumer;

import com.tsukihi.myrpc.config.RpcConfig;
import com.tsukihi.myrpc.utils.ConfigUtils;

/**
 * 简单的消费者示例
 */
public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
