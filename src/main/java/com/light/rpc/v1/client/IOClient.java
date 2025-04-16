package com.light.rpc.v1.client;

import com.light.rpc.v1.common.pojo.RpcRequest;
import com.light.rpc.v1.common.pojo.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 底层通信
 * 负责底层与服务器的通信，发送request，返回response
 * 建立连接、发送请求、接收响应、异常处理
 */
public class IOClient {

    // 传入的是服务器相关信息
    public static RpcResponse sendRequest(String host, int port, RpcRequest request) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(request);
            out.flush();

            return (RpcResponse) in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
