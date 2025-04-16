package com.light.rpc.v1.server.provider.impl;

import com.light.rpc.v1.server.provider.server.RpcServer;
import com.light.rpc.v1.server.provider.server.ServiceProvider;
import com.light.rpc.v1.server.provider.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRpcServer implements RpcServer {

    private final ThreadPoolExecutor threadPool;
    private ServiceProvider serviceProvider;

    public ThreadPoolRpcServer(ServiceProvider serviceProvider) {
        threadPool = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                1000,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100)
        );

        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        System.out.println("服务端启动了");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkThread(socket, serviceProvider));
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
