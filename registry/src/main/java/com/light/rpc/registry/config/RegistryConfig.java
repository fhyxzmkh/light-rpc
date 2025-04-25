package com.light.rpc.registry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "rpc.registry")
public class RegistryConfig {
    private String address = "10.100.164.20";
    private int port = 2181;
    private String rootPath = "MyRPC";
    private int sessionTimeout = 40000;
    private boolean autoStart = true;
}