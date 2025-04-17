package com.light.rpc.v2.test;

import com.light.rpc.v2.common.service.UserService;
import com.light.rpc.v2.common.service.impl.UserServiceImpl;
import com.light.rpc.v2.server.rpcServer.RpcServer;
import com.light.rpc.v2.server.provider.ServiceProvider;
import com.light.rpc.v2.server.rpcServer.impl.NettyRPCRPCServer;

public class TestServer {
    public static void main(String[] args) {
        UserService userService=new UserServiceImpl();

        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer=new NettyRPCRPCServer(serviceProvider);
        rpcServer.start(9999);
    }
}
