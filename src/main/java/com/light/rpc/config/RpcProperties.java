package com.light.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RPC配置属性类
 */
@ConfigurationProperties(prefix = "light.rpc")
@Data
public class RpcProperties {

    /**
     * 注册中心地址，例如：127.0.0.1:2181
     */
    private String registryAddress;

    /**
     * 服务器地址，例如：127.0.0.1:8000
     */
    private String serverAddress;

    /**
     * 直连服务器地址，例如：127.0.0.1:8000
     */
    private String directServerAddress;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本
     */
    private String serviceVersion;
}
