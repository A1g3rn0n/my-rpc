package com.tsukihi.myrpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo序列化器
 */
@Slf4j
public class KryoSerializer implements Serializer{

    /**
     * kryo线程不安全，使用ThreadLocal保证每个线程只有一个kryo对象
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kyro = new Kryo();
        // 设置动态序列化和反序列化类， 不提前注册所有类（可能有安全问题）
        kyro.setRegistrationRequired(false);
        log.info("Kryo {} has instance: {}", Thread.currentThread().getId(), kyro);
        return kyro;
    });

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, classType);
        input.close();
        return result;
    }
}
