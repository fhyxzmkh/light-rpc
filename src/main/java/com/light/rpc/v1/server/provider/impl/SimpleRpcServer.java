package com.light.rpc.v1.server.provider.impl;

import com.light.rpc.v1.server.provider.server.RpcServer;
import com.light.rpc.v1.server.provider.server.ServiceProvider;
import com.light.rpc.v1.server.provider.work.WorkThread;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class SimpleRpcServer implements RpcServer {

    private ServiceProvider serviceProvider;


    @Override
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器启动了");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        // TODO
    }
}
