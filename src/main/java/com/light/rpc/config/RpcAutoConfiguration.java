package com.light.rpc.config;

import com.light.rpc.client.RpcProxy;
import com.light.rpc.common.annotation.RpcReference;
import com.light.rpc.common.annotation.RpcService;
import com.light.rpc.registry.ServiceDiscovery;
import com.light.rpc.registry.ServiceRegistry;
import com.light.rpc.registry.zookeeper.ZooKeeperServiceDiscovery;
import com.light.rpc.registry.zookeeper.ZooKeeperServiceRegistry;
import com.light.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

/**
 * RPC自动配置类
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
@Slf4j
public class RpcAutoConfiguration implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Autowired
    private RpcProperties rpcProperties;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 配置ZooKeeper服务注册中心
     */
    @Bean
    @ConditionalOnProperty(prefix = "light.rpc", name = "registry-address")
    public ServiceRegistry serviceRegistry() {
        return new ZooKeeperServiceRegistry(rpcProperties.getRegistryAddress());
    }

    /**
     * 配置ZooKeeper服务发现中心
     */
    @Bean
    @ConditionalOnProperty(prefix = "light.rpc", name = "registry-address")
    public ServiceDiscovery serviceDiscovery() {
        return new ZooKeeperServiceDiscovery(rpcProperties.getRegistryAddress());
    }

    /**
     * 配置RPC服务器
     */
    @Bean
    @ConditionalOnBean(ServiceRegistry.class)
    @ConditionalOnProperty(prefix = "light.rpc", name = "server-address")
    public RpcServer rpcServer(ServiceRegistry serviceRegistry) {
        return new RpcServer(rpcProperties.getServerAddress(), serviceRegistry);
    }

    /**
     * 配置RPC代理（服务发现模式）
     */
    @Bean
    @ConditionalOnBean(ServiceDiscovery.class)
    public RpcProxy rpcDiscoveryProxy(ServiceDiscovery serviceDiscovery) {
        return new RpcProxy(serviceDiscovery);
    }

    /**
     * 配置RPC代理（直连模式）
     */
    @Bean
    @ConditionalOnProperty(prefix = "light.rpc", name = "direct-server-address")
    public RpcProxy rpcDirectProxy() {
        return new RpcProxy(rpcProperties.getDirectServerAddress());
    }

    /**
     * Bean后处理器，用于处理RpcReference注解
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 处理bean中所有标记了RpcReference注解的字段
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                RpcReference reference = field.getAnnotation(RpcReference.class);
                field.setAccessible(true);
                
                try {
                    // 判断是直连模式还是服务发现模式
                    RpcProxy rpcProxy;
                    if (reference.directAddress()) {
                        // 直连模式
                        String address = reference.address();
                        if (StringUtils.isEmpty(address)) {
                            address = rpcProperties.getDirectServerAddress();
                        }
                        if (StringUtils.isEmpty(address)) {
                            throw new IllegalArgumentException("RPC直连地址未配置");
                        }
                        rpcProxy = new RpcProxy(address);
                    } else {
                        // 服务发现模式
                        String registryAddress = reference.registryAddress();
                        if (StringUtils.isEmpty(registryAddress)) {
                            registryAddress = rpcProperties.getRegistryAddress();
                        }
                        if (StringUtils.isEmpty(registryAddress)) {
                            throw new IllegalArgumentException("RPC注册中心地址未配置");
                        }
                        ServiceDiscovery serviceDiscovery = new ZooKeeperServiceDiscovery(registryAddress);
                        rpcProxy = new RpcProxy(serviceDiscovery);
                    }
                    
                    // 创建接口代理对象
                    Object serviceProxy = rpcProxy.create(field.getType(), reference.version());
                    
                    // 将代理对象注入到字段
                    field.set(bean, serviceProxy);
                    
                    log.info("注入RPC代理: {}.{}", bean.getClass().getName(), field.getName());
                } catch (Exception e) {
                    log.error("创建RPC客户端代理失败", e);
                }
            }
        }
        
        return bean;
    }
}
