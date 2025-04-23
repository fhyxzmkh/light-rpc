package com.light.rpc.client.proxy;

import com.light.rpc.client.config.ClientConfig;
import com.light.rpc.client.rpcClient.RpcClient;
import com.light.rpc.common.pojo.RpcRequest;
import com.light.rpc.common.pojo.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

@Slf4j
public class ClientProxy implements InvocationHandler {

    private RpcClient rpcClient;
    private ClientConfig config;
    private Map<String, Object> localServices;
    private boolean useLocalCall;
    
    public ClientProxy(RpcClient rpcClient, ClientConfig config, Map<String, Object> localServices) {
        this.rpcClient = rpcClient;
        this.config = config;
        this.localServices = localServices;
        this.useLocalCall = config.isUseLocalCall();
    }
    
    public void setUseLocalCall(boolean useLocalCall) {
        this.useLocalCall = useLocalCall;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String interfaceName = method.getDeclaringClass().getName();
        
        // 如果配置了使用本地调用，并且存在本地服务，则直接调用本地服务
        if (useLocalCall && localServices.containsKey(interfaceName)) {
            log.debug("使用本地调用: {}.{}", interfaceName, method.getName());
            return method.invoke(localServices.get(interfaceName), args);
        }
        
        // 否则使用远程调用
        log.debug("使用远程调用: {}.{}", interfaceName, method.getName());
        RpcRequest request = RpcRequest.builder()
                .interfaceName(interfaceName)
                .methodName(method.getName())
                .params(args)
                .paramsType(method.getParameterTypes())
                .build();

        RpcResponse response = rpcClient.sendRequest(request);
        if (response == null) {
            throw new RuntimeException("远程调用失败，未获得响应");
        }
        
        if (response.getCode() != 200) {
            throw new RuntimeException("远程调用失败: " + response.getMessage());
        }
        
        return response.getData();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(), 
                new Class[]{clazz}, 
                this);
    }
}