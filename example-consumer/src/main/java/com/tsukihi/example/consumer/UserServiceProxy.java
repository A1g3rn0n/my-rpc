package com.tsukihi.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.tsukihi.example.common.model.User;
import com.tsukihi.example.common.service.UserService;
import com.tsukihi.myrpc.model.RpcRequest;
import com.tsukihi.myrpc.model.RpcResponse;
import com.tsukihi.myrpc.serializer.JdkSerializer;
import com.tsukihi.myrpc.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 *
 * 静态代理是在编译时就确定了代理类，
 * 每一个被代理的类都需要一个对应的代理类。
 * 代理类和被代理类在编译时就已经确定下来，是硬编码在程序中的。
 */
public class UserServiceProxy implements UserService {

    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();


        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        // 发送请求
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080/")
                    .body(bodyBytes)
                    .execute()) {
                // 获取结果
                result = httpResponse.bodyBytes();
            }
            // 反序列化结果
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
