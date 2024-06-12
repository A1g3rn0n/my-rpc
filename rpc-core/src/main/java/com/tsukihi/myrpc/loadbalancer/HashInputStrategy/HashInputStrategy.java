package com.tsukihi.myrpc.loadbalancer.HashInputStrategy;

import com.tsukihi.myrpc.model.RpcRequest;

import java.util.Map;

public interface HashInputStrategy {
    Map<String, Object> getHashInput(RpcRequest rpcRequest);
}
