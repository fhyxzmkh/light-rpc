package com.light.rpc.v1.test;

import com.light.rpc.v1.common.service.UserService;
import com.light.rpc.v1.common.service.impl.UserServiceImpl;
import com.light.rpc.v1.server.provider.impl.ThreadPoolRpcServer;
import com.light.rpc.v1.server.provider.server.RpcServer;
import com.light.rpc.v1.server.provider.server.ServiceProvider;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new ThreadPoolRpcServer(serviceProvider);

        rpcServer.start(9999);
    }
}
