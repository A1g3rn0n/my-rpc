package com.tsukihi.myrpc.fault.tolerant;

import com.tsukihi.myrpc.model.RpcResponse;
import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 */
public class FailBackTolerantStrategy implements TolerantStrategy{

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 带实现
        return null;
    }
}
