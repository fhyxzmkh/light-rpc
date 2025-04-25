package com.light.rpc.registry.config;

import jakarta.annotation.Resource;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Curator 配置类，用于创建 CuratorFramework Bean
 */
@Configuration
public class CuratorConfig {
    
    @Resource
    private RegistryConfig registryConfig;
    
    @Bean
    public CuratorFramework curatorFramework() {
        // 指数时间重试
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        
        String connectString = registryConfig.getAddress() + ":" + registryConfig.getPort();
        
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .sessionTimeoutMs(registryConfig.getSessionTimeout())
                .retryPolicy(policy)
                .namespace(registryConfig.getRootPath())
                .build();
                
        // 注意：这里不自动启动，让 RegistryCenter 控制启动
        
        return client;
    }
}
