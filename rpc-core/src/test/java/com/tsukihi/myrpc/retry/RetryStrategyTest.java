package com.tsukihi.myrpc.retry;

import com.tsukihi.myrpc.fault.retry.FixedIntervalRetryStrategy;
import com.tsukihi.myrpc.fault.retry.NoRetryStrategy;
import com.tsukihi.myrpc.fault.retry.RetryStrategy;
import com.tsukihi.myrpc.model.RpcResponse;
import org.junit.Test;

/**
 * 重试策略测试
 */
public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try{
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);

        }catch (Exception e){
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}
