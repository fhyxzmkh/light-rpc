package com.light.rpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "rpc.server")
public class ServerConfig {
    private String host = "127.0.0.1";
    private int port = 9999;
    private String registryAddress = "10.100.164.20";
    private int registryPort = 2181;
    private boolean autoStart = true;
}