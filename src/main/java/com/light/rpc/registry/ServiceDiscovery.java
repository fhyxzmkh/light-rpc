package com.light.rpc.registry;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {

    /**
     * 发现服务
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    String discover(String serviceName);
    
    /**
     * 关闭服务发现
     */
    void close();
}
