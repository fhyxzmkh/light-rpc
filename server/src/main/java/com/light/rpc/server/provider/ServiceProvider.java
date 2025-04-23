package com.light.rpc.server.provider;

import com.light.rpc.server.config.ServerConfig;
import com.light.rpc.server.serviceRegister.ServiceRegister;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ServiceProvider {

    private Map<String, Object> interfaceProvider = new HashMap<>();
    
    @Resource
    private ServerConfig serverConfig;
    
    @Resource
    private ServiceRegister serviceRegister;
    
    @PostConstruct
    public void init() {
        log.info("服务提供者初始化，地址: {}:{}", serverConfig.getHost(), serverConfig.getPort());
    }

    public void provideServiceInterface(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for (Class<?> clazz : interfaces) {
            String interfaceName = clazz.getName();
            //本机的映射表
            interfaceProvider.put(interfaceName, service);
            //在注册中心注册服务
            serviceRegister.register(interfaceName, 
                    new InetSocketAddress(serverConfig.getHost(), serverConfig.getPort()));
            log.info("注册服务: {}", interfaceName);
        }
    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}