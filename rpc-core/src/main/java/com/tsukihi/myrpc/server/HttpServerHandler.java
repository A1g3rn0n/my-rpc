package com.tsukihi.myrpc.server;

import com.tsukihi.myrpc.RpcApplication;
import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.model.RpcResponse;
import com.tsukihi.myrpc.registry.LocalRegistry;
import com.tsukihi.myrpc.serializer.JdkSerializer;
import com.tsukihi.myrpc.serializer.Serializer;
import com.tsukihi.myrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request){
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 记录日志
        log.info(String.format("Received request: %s %s", request.method(), request.uri()));

        // 异步处理Http请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try{
                // 反序列化请求
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            }catch (Exception e){
                e.printStackTrace();
            }

            // 构造相应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为null，直接返回
            if(rpcRequest == null) {
                rpcResponse.setMessage("RpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应请求
     *
     *
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");

        try{
            // 序列化
            byte[] serializerd = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serializerd));
        }catch (IOException e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
