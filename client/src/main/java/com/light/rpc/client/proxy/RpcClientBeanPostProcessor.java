package com.light.rpc.client.proxy;

import com.light.rpc.client.annotation.RpcReference;
import com.light.rpc.client.config.ClientConfig;
import com.light.rpc.client.rpcClient.RpcClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RpcClientBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private ClientConfig clientConfig;
    
    @Resource
    private RpcClient rpcClient;
    
    private Map<String, Object> localServices = new ConcurrentHashMap<>();
    
    // 注册本地服务
    public void registerLocalService(Class<?> interfaceClass, Object implementation) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("只能注册接口类型");
        }
        if (!interfaceClass.isInstance(implementation)) {
            throw new IllegalArgumentException("实现类必须实现指定的接口");
        }
        localServices.put(interfaceClass.getName(), implementation);
        log.info("注册本地服务: {}", interfaceClass.getName());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                field.setAccessible(true);
                
                Class<?> fieldType = field.getType();
                if (!fieldType.isInterface()) {
                    throw new IllegalStateException("RpcReference只能用于接口字段: " + field.getName());
                }
                
                try {
                    // 创建代理对象
                    Object proxy = createProxy(fieldType, rpcReference.useLocalCall());
                    // 注入代理对象
                    field.set(bean, proxy);
                    log.info("注入RPC代理: {}#{} -> {}", beanName, field.getName(), fieldType.getName());
                } catch (IllegalAccessException e) {
                    log.error("注入RPC代理失败", e);
                }
            }
        }
        
        return bean;
    }
    
    private <T> T createProxy(Class<T> interfaceClass, boolean useLocalCall) {
        ClientProxy clientProxy = new ClientProxy(rpcClient, clientConfig, localServices);
        clientProxy.setUseLocalCall(useLocalCall);
        return clientProxy.getProxy(interfaceClass);
    }
}