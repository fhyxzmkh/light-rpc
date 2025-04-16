package com.light.rpc.v1.server.provider.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地服务存放器（注册中心）
 * 负责本地服务的注册和查找
 */
public class ServiceProvider {

    // 集中存放服务的实例，接口的全限定名作为key，接口对应的实现类实例作为value
    private final Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        interfaceProvider = new HashMap<>();
    }

    // 本地注册服务
    public void provideServiceInterface(Object service) { // 接受一个服务实例
        String serviceName = service.getClass().getName(); // 获取服务对象的完整类名
        Class<?>[] interfaceName = service.getClass().getInterfaces(); // 获取服务对象实现的所有接口

        for (Class<?> i : interfaceName) {
            interfaceProvider.put(i.getName(), service); // 将接口的全限定名和对应的服务实例存入map
        }
    }

    // 获取服务实例
    public Object getService(String serviceName) {
        return interfaceProvider.get(serviceName);
    }

}
