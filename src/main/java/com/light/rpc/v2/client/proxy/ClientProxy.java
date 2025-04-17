package com.light.rpc.v2.client.proxy;

import com.light.rpc.v2.client.rpcClient.RpcClient;
import com.light.rpc.v2.client.rpcClient.impl.NettyRpcClient;
import com.light.rpc.v2.common.pojo.RpcRequest;
import com.light.rpc.v2.common.pojo.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;

    public ClientProxy(String host, int port) {
        this.rpcClient = new NettyRpcClient(host, port);
    }

    // jdk动态代理，每一次代理对象调用方法，都会经过此方法增强
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();

        RpcResponse response = rpcClient.sendRequest(request);
        return response.getData();
    }

    // 动态生成一个实现指定接口的代理对象
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }

}
