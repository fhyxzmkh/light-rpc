package com.light.rpc.registry.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RegistryCenter {

    @Resource
    private CuratorFramework client;
    
    @Resource
    private RegistryConfig config;
    
    @PostConstruct
    public void init() {
        if (config.isAutoStart()) {
            start();
        }
    }
    
    public void start() {
        String connectString = config.getAddress() + ":" + config.getPort();
        client.start();
        log.info("注册中心已启动: {}", connectString);
    }
    
    @PreDestroy
    public void stop() {
        if (client != null) {
            client.close();
            log.info("注册中心已停止");
        }
    }
    
    public CuratorFramework getClient() {
        return client;
    }
    
    /**
     * 检查注册中心是否正在运行
     * @return 如果注册中心正在运行，则返回true；否则返回false
     */
    public boolean isRunning() {
        return client != null && client.getZookeeperClient().isConnected();
    }
}