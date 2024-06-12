package com.tsukihi.myrpc.loadbalancer.HashInputStrategy;

import com.tsukihi.myrpc.spi.SpiLoader;

/**
 * Hash input strategy factory.
 */
public class HashInputStrategyFactory {

    static {
         SpiLoader.load(HashInputStrategy.class);
    }

    /**
     * 默认的hash输入策略
     */
    private static final HashInputStrategy DEFAULT_HASH_INPUT_STRATEGY = new HashInputWithMethodNameStrategy();

    /**
     * 获取hash输入策略实例
     * @param key hash输入策略key
     * @return hash输入策略实例
     */
    public static HashInputStrategy getInstance(String key) {
        return SpiLoader.getInstance(HashInputStrategy.class, key);
    }
}
