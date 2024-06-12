package com.tsukihi.myrpc.serializer;

import com.tsukihi.myrpc.spi.SpiLoader;


/**
 * 序列化工厂
 * 序列化器对象是可以复用的，没必要每次执行序列化操作前都创建一个新的序列化器对象
 * 使用设计模式中的工厂模式 + 单例模式来简化创建和获取序列化器对象的操作
 */
public class SerializerFactory {

    /*
     * 使用静态代码块，在工厂首次加载时，
     * 就会调用 SpiLoader 的 load 方法加载序列化器接口的所有实现类，
     * 之后就可以通过调用 getInstance 方法获取指定的实现类对象了。
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     * @param key 序列化器key
     * @return 序列化器实例
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
