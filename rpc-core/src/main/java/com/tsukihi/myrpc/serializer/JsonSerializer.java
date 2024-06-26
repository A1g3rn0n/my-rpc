package com.tsukihi.myrpc.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.model.RpcResponse;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * JSON序列化器
 */
public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException{
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException{
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if(obj instanceof RpcRequest){
            return handleRequest((RpcRequest) obj, classType);
        }
        if(obj instanceof RpcResponse){
            return handleResponse((RpcResponse) obj, classType);
        }
        return obj;
    }

    /**
     * 由于Object 的原始对象会被擦除，导致反序列化时会被作为LinkedHashMap无法转化为原始对象
     * 因此需要做特殊处理
     *
     * @param rpcRequest 请求对象
     * @param type 类型
     * @return {@link T}
     * @throws IOException IO异常
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        // 循环处理每个参数类型
        for(int i = 0; i < parameterTypes.length; i++){
            Class<?> clazz = parameterTypes[i];
            // 如果类型不同，则重新处理类型
            if(!clazz.isAssignableFrom(args[i].getClass())){
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }

        return type.cast(rpcRequest);
    }

    /**
     * 由于Object的原始对象会被擦除，导致反序列化时会被作为LinkedHashMap无法转化为原始对象，因此特殊处理
     *
     * @param rpcResponse 响应对象
     * @param type 类型
     * @return {@link T}
     * @throws IOException IO异常
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        // 处理响应数据
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));

        return type.cast(rpcResponse);
    }
}
