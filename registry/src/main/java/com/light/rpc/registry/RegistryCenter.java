package com.light.rpc.registry;

import com.light.rpc.registry.config.RegistryConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegistryCenter {
    
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
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        
        String connectString = config.getAddress() + ":" + config.getPort();
        
        this.client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(config.getSessionTimeout())
                .retryPolicy(policy)
                .namespace(config.getRootPath())
                .build();
                
        this.client.start();
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
}