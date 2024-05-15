package com.tsukihi.myrpc.server.tcp;

import com.tsukihi.myrpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Vert.x 实现的 TCP 网络传输服务器
 */
@Slf4j
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData){
        // 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
        System.out.println("Received data from client: " + new String(requestData));
        return "Hello, client".getBytes();
    }


    @Override
    public void doStart(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        // 创建TCP服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpServerHandler());
//        server.connectHandler(socket -> {
//            // 构造parser
//            // 为 Parser 指定每次读取固定值长度的内容
//            RecordParser parser = RecordParser.newFixed(8);
//            parser.setOutput(new Handler<Buffer>() {
//                // 初始化
//                 int size =  -1;
//                 Buffer resultBuffer = Buffer.buffer();
//
//                 @Override
//                 public void handle(Buffer buffer){
//                     if(-1 == size){
//                         // 读取消息体长度
//                         size = buffer.getInt(4);
//                         parser.fixedSizeMode(size);
//                         // 写入头信息到结果
//                         resultBuffer.appendBuffer(buffer);
//                     }else{
//                         // 写入体信息到结果
//                         resultBuffer.appendBuffer(buffer);
//                         System.out.println(resultBuffer.toString());
//                         // 重置一轮
//                         parser.fixedSizeMode(8);
//                         size = -1;
//                         resultBuffer = Buffer.buffer();
//                     }
//
//                 }
//            });
//
//            socket.handler(parser);


//                // 处理接收到的字节数组
//                byte[] requestData = buffer.getBytes();
//                // 在这里进行自定义的字节数组处理逻辑
//                byte[] responseData = handleRequest(requestData);
//                // 发送响应
//                // 向连接到服务器的客户端发送数据。
//                // 注意发送的数据格式为 Buffer，这是 Vert.x 为我们提供的字节数组缓冲区实现。
//                socket.write(Buffer.buffer(responseData));
//        });


        // 启动TCP服务器并监听指定端口
        server.listen(port, result ->{
            if(result.succeeded()) {
                System.out.println("TCP server started on port " + port);
            } else {
                System.out.println("TCP server failed to start");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
