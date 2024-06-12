package com.tsukihi.myrpc.fault.tolerant;

import com.tsukihi.myrpc.spi.SpiLoader;

/**
 * 容错策略工厂 用于获取容错器对象
 */
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取实例
     *
     * @param key 键
     * @return 实例
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
