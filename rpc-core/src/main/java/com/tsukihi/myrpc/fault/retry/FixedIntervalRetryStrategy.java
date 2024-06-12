package com.tsukihi.myrpc.fault.retry;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Attempt;

import com.tsukihi.myrpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定间隔时间 重试策略
 */

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{


    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 重试条件 使用retryIfExceptionOfType方法，指定出现Exception异常时重试
                .retryIfExceptionOfType(Exception.class)
                // 重试等待策略，使用withWaitStrategy方法指定策略，选择fixedWait固定时间间隔策略
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                // 重试停止策略，使用withStopStrategy方法指定策略，选择stopAfterAttempt超过最大重试次数停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 重试工作，使用withRetryListener方法监听重试，每次重试时，除了再次执行任务外，打印当前重试次数
                // todo 测试可以正常，但是run会抛出异常ClassNotFound RetryListener
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
