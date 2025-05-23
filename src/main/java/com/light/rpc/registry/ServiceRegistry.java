package com.light.rpc.registry;

/**
 * 服务注册接口
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);
    
    /**
     * 注销服务
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    void unregister(String serviceName, String serviceAddress);
    
    /**
     * 关闭注册中心
     */
    void close();
}
