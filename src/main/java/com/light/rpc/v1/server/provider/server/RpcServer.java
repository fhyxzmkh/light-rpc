package com.light.rpc.v1.server.provider.server;

public interface RpcServer {
    void start(int port);
    void stop();
}
