package com.tsukihi.myrpc.loadbalancer.HashInputStrategy;

import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.utils.ClientInfoUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Hash input strategy with IP address.
 */
public class HashInputWithIPStrategy implements HashInputStrategy{
    @Override
    public Map<String, Object> getHashInput(RpcRequest rpcRequest) {
        Map<String, Object> requestParams = new HashMap<>();
        // todo
        requestParams.put("clientIP", ClientInfoUtils.getInstance().getIpAddress());
        return requestParams;
    }
}
