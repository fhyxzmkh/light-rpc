package com.light.rpc.registry.zookeeper;

import com.light.rpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于ZooKeeper的服务发现实现
 */
@Slf4j
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final String ZK_REGISTRY_PATH = "/light-rpc";
    private final CuratorFramework client;
    
    // 服务地址缓存：服务名称 -> 地址列表
    private final Map<String, List<String>> serviceAddressCache = new ConcurrentHashMap<>();

    public ZooKeeperServiceDiscovery(String zkAddress) {
        // 创建ZooKeeper客户端
        client = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        
        // 启动客户端
        client.start();
        log.info("连接ZooKeeper服务发现成功: {}", zkAddress);
    }

    @Override
    public String discover(String serviceName) {
        // 先从缓存中获取服务地址列表
        List<String> addressList = serviceAddressCache.get(serviceName);
        
        if (CollectionUtils.isEmpty(addressList)) {
            // 缓存中没有，从ZooKeeper获取
            String servicePath = ZK_REGISTRY_PATH + "/" + serviceName;
            try {
                // 获取服务地址列表
                addressList = client.getChildren().forPath(servicePath);
                
                // 将地址列表放入缓存
                serviceAddressCache.put(serviceName, addressList);
                
                // 注册监听
                registerWatcher(serviceName, servicePath);
                
                log.info("服务发现成功: {}, 地址: {}", serviceName, addressList);
            } catch (Exception e) {
                log.error("获取服务地址列表失败: {}", serviceName, e);
                throw new RuntimeException("获取服务地址列表失败: " + serviceName, e);
            }
        }
        
        // 如果地址列表为空，抛出异常
        if (CollectionUtils.isEmpty(addressList)) {
            throw new RuntimeException("没有找到服务提供者: " + serviceName);
        }
        
        // 负载均衡：随机选择一个地址
        return addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
    }

    /**
     * 注册服务地址变化监听器
     */
    private void registerWatcher(String serviceName, String servicePath) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, servicePath, true);
        pathChildrenCache.getListenable().addListener((client, event) -> {
            // 子节点变化时更新缓存
            if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED || 
                event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED ||
                event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                
                // 重新获取服务地址列表
                List<String> newAddressList = new ArrayList<>();
                for (String childPath : client.getChildren().forPath(servicePath)) {
                    newAddressList.add(childPath);
                }
                
                // 更新缓存
                serviceAddressCache.put(serviceName, newAddressList);
                
                log.info("服务地址列表已更新: {}, 地址: {}", serviceName, newAddressList);
            }
        });
        pathChildrenCache.start();
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            log.info("关闭ZooKeeper连接");
        }
    }
}
