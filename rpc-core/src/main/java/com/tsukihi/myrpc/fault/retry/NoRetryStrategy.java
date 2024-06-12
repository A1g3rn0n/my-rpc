package com.tsukihi.myrpc.fault.retry;

import com.tsukihi.myrpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试 的策略
 */
public class NoRetryStrategy implements RetryStrategy{

    /**
     * 重试
     * @param callable 任务
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
