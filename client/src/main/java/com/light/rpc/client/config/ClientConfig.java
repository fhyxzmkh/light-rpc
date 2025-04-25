package com.light.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rpc.client")
public class ClientConfig {
    private String registryAddress = "10.100.164.20";
    private int registryPort = 2181;
    private int timeout = 5000;
    private boolean useLocalCall = false;
}