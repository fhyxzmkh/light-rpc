package com.light.rpc.registry.zookeeper;

import com.light.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 基于ZooKeeper的服务注册实现
 */
@Slf4j
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private static final String ZK_REGISTRY_PATH = "/light-rpc";
    private CuratorFramework client;

    public ZooKeeperServiceRegistry(String zkAddress) {
        // 创建ZooKeeper客户端
        client = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        
        // 启动客户端
        client.start();
        log.info("连接ZooKeeper注册中心成功: {}", zkAddress);
        
        try {
            // 创建注册根节点（持久节点）
            Stat stat = client.checkExists().forPath(ZK_REGISTRY_PATH);
            if (stat == null) {
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.PERSISTENT)
                      .forPath(ZK_REGISTRY_PATH);
                log.info("创建ZooKeeper注册中心根节点: {}", ZK_REGISTRY_PATH);
            }
        } catch (Exception e) {
            log.error("创建ZooKeeper注册中心根节点失败", e);
            throw new RuntimeException("创建ZooKeeper注册中心根节点失败", e);
        }
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        try {
            // 创建服务节点路径（持久节点）
            String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
            Stat stat = client.checkExists().forPath(servicePath);
            if (stat == null) {
                client.create()
                      .creatingParentsIfNeeded()
                      .withMode(CreateMode.PERSISTENT)
                      .forPath(servicePath);
                log.info("创建服务节点: {}", servicePath);
            }
            
            // 创建地址节点（临时节点）
            String addressPath = servicePath + "/" + serviceAddress;
            String addressNode = client.create()
                                      .withMode(CreateMode.EPHEMERAL)
                                      .forPath(addressPath);
            log.info("创建地址节点: {}", addressNode);
        } catch (Exception e) {
            log.error("注册服务失败: {} - {}", serviceName, serviceAddress, e);
            throw new RuntimeException("注册服务失败", e);
        }
    }

    @Override
    public void unregister(String serviceName, String serviceAddress) {
        try {
            String addressPath = ZK_REGISTRY_PATH + "/" + serviceName + "/" + serviceAddress;
            client.delete().forPath(addressPath);
            log.info("删除地址节点: {}", addressPath);
        } catch (Exception e) {
            log.error("注销服务失败: {} - {}", serviceName, serviceAddress, e);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            log.info("关闭ZooKeeper连接");
        }
    }
}
