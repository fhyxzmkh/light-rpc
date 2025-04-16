package com.light.rpc.v1.client.proxy;

import com.light.rpc.v1.client.IOClient;
import com.light.rpc.v1.common.pojo.RpcRequest;
import com.light.rpc.v1.common.pojo.RpcResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private String host;
    private int port;

    // jdk动态代理，每一次代理对象调用方法，都会经过此方法增强
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();

        RpcResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }

    // 动态生成一个实现指定接口的代理对象
    public <T>T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }

}
