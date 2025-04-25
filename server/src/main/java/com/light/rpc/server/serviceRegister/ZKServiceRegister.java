package com.light.rpc.server.serviceRegister;

import com.light.rpc.registry.config.RegistryCenter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class ZKServiceRegister implements ServiceRegister {

    private RegistryCenter registryCenter;

    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            CuratorFramework client = registryCenter.getClient();
            // serviceName创建成永久节点，服务提供者下线时，不删服务名，只删地址
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
            }
            // 路径地址，一个/代表一个节点
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);
            // 临时节点，服务器下线就删除节点
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path);
            log.info("服务注册成功: {}", path);
        } catch (Exception e) {
            log.warn("服务已存在: {}", serviceName);
        }
    }

    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }
}