package com.tsukihi.myrpc.fault.tolerant;

import com.tsukihi.myrpc.model.RpcResponse;
import java.util.Map;

/**
 * 转移到其他服务节点 - 容错策略
 */
public class FailOverTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 带实现
        return null;
    }
}
