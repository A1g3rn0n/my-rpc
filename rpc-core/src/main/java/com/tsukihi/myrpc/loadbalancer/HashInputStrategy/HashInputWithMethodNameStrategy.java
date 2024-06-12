package com.tsukihi.myrpc.loadbalancer.HashInputStrategy;

import com.tsukihi.myrpc.model.RpcRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Hash input strategy with method name.
 */
public class HashInputWithMethodNameStrategy implements HashInputStrategy{
    @Override
    public Map<String, Object> getHashInput(RpcRequest rpcRequest) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        return requestParams;
    }
}
