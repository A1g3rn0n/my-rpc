package com.tsukihi.myrpc.fault.retry;

import com.tsukihi.myrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {

    /**
     * 重试
     *
     * @param callable 任务
     * @return 结果
     * @throws Exception 异常
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
