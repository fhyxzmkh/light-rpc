package com.light.rpc.client;

import com.light.rpc.common.bean.RpcRequest;
import com.light.rpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC代理
 * 用于创建RPC服务代理
 */
@Slf4j
public class RpcProxy {

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    
    // 连接池，避免重复创建连接
    private Map<String, RpcClient> clientPool = new ConcurrentHashMap<>();
    
    // 默认的远程调用超时时间，单位毫秒
    private static final long DEFAULT_TIMEOUT = 5000;

    /**
     * 使用直连模式创建RPC代理
     * 
     * @param serverAddress 服务地址
     */
    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * 使用服务发现模式创建RPC代理
     * 
     * @param serviceDiscovery 服务发现接口
     */
    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建RPC服务代理
     * 
     * @param interfaceClass 接口类
     * @param <T> 接口泛型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return create(interfaceClass, "");
    }

    /**
     * 创建带版本的RPC服务代理
     * 
     * @param interfaceClass 接口类
     * @param serviceVersion 服务版本
     * @param <T> 接口泛型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass, String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建RPC请求对象
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        request.setVersion(serviceVersion);
                        
                        // 获取RPC服务地址
                        String serviceAddress = "";
                        if (serviceDiscovery != null) {
                            // 基于服务发现获取服务地址
                            String serviceName = interfaceClass.getName();
                            if (serviceVersion != null && !serviceVersion.isEmpty()) {
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            log.debug("发现服务地址: {}", serviceAddress);
                        } else if (serverAddress != null) {
                            // 直连模式
                            serviceAddress = serverAddress;
                        }
                        
                        if (serviceAddress == null || serviceAddress.isEmpty()) {
                            throw new RuntimeException("未找到服务地址");
                        }
                        
                        // 从池中获取RPC客户端
                        RpcClient client = getClient(serviceAddress);
                        
                        // 发送RPC请求
                        try {
                            RpcClientHandler.RpcFuture rpcFuture = client.send(request);
                            // 等待RPC结果（默认超时时间）
                            return rpcFuture.get(DEFAULT_TIMEOUT);
                        } catch (Exception e) {
                            log.error("RPC调用失败", e);
                            throw e;
                        }
                    }
                }
        );
    }

    /**
     * 从连接池获取或创建RPC客户端
     */
    private RpcClient getClient(String serviceAddress) throws InterruptedException {
        // 从连接池获取客户端
        RpcClient client = clientPool.get(serviceAddress);
        
        // 如果客户端不存在，创建新的客户端
        if (client == null) {
            client = new RpcClient(serviceAddress);
            client.connect();
            clientPool.put(serviceAddress, client);
        }
        
        return client;
    }

    /**
     * 关闭所有RPC客户端连接
     */
    public void close() {
        for (RpcClient client : clientPool.values()) {
            client.close();
        }
        clientPool.clear();
    }
}
