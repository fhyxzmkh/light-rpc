package com.light.rpc.v1.server.provider.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地服务存放器（注册中心）
 * 负责本地服务的注册和查找
 */
public class ServiceProvider {

    // 集中存放服务的实例，接口的全限定名作为key，服务的实例作为value
    private final Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        interfaceProvider = new HashMap<>();
    }

    // 本地注册服务
    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for (Class<?> i : interfaceName) {
            interfaceProvider.put(i.getName(), service);
        }
    }

    // 获取服务实例
    public Object getService(String serviceName) {
        return interfaceProvider.get(serviceName);
    }

}
