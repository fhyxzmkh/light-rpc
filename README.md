# Light-RPC框架

Light-RPC是一个轻量级的Java RPC框架，基于Netty、ZooKeeper和Protostuff实现，提供了简单易用的远程调用功能。

## 特性

- 支持服务端、客户端和注册中心三种角色
- 基于Netty的高性能网络通信
- 基于ZooKeeper的服务注册与发现
- 基于Protostuff的高效序列化
- 支持Spring Boot自动配置
- 支持注解方式声明服务和引用
- 支持本地调用和远程调用的灵活切换
- 低侵入性，对业务代码影响小

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>com</groupId>
    <artifactId>light-rpc</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. 定义服务接口

```java
public interface HelloService {
    String hello(String name);
}
```

### 3. 实现服务接口

```java
import com.light.rpc.common.annotation.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}
```

### 4. 服务提供方配置

在`application.properties`中添加：

```properties
# ZooKeeper注册中心地址
light.rpc.registry-address=127.0.0.1:2181
# 服务发布地址
light.rpc.server-address=127.0.0.1:8000
```

### 5. 服务消费方引用服务

```java
import com.light.rpc.common.annotation.RpcReference;
import org.springframework.stereotype.Component;

@Component
public class HelloController {

    @RpcReference(version = "1.0")
    private HelloService helloService;
    
    public String sayHello(String name) {
        return helloService.hello(name);
    }
}
```

### 6. 服务消费方配置

在`application.properties`中添加：

```properties
# ZooKeeper注册中心地址
light.rpc.registry-address=127.0.0.1:2181
```

## 配置选项

### 注册中心（ZooKeeper）

```properties
# ZooKeeper注册中心地址
light.rpc.registry-address=127.0.0.1:2181
```

### 服务提供方

```properties
# 服务发布地址
light.rpc.server-address=127.0.0.1:8000
```

### 服务消费方

```properties
# 直连模式服务地址（不使用注册中心时配置）
light.rpc.direct-server-address=127.0.0.1:8000

# 服务名称和版本
light.rpc.service-name=exampleService
light.rpc.service-version=1.0.0
```

## 本地调用与远程调用切换

1. 远程调用模式：使用`@RpcReference`注解引用服务
2. 本地调用模式：直接注入本地服务实现（去掉`@RpcReference`注解）

## 服务启动

1. 注册中心：部署ZooKeeper服务器
2. 服务提供方：启动带有`@RpcService`注解服务实现的应用
3. 服务消费方：启动带有`@RpcReference`注解引用服务的应用

## 注意事项

1. 确保ZooKeeper服务正常运行
2. 服务接口需要在服务提供方和消费方共享
3. 远程调用的参数和返回值必须可序列化
