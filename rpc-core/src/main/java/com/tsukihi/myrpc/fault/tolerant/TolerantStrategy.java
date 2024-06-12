package com.tsukihi.myrpc.fault.tolerant;

import com.tsukihi.myrpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略
 */
public interface TolerantStrategy {

    /**
     * 容错
     *
     * @param context 上下文 用于传递数据
     * @param e 异常
     * @return 响应
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
