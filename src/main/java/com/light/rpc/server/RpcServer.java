package com.light.rpc.server;

import com.light.rpc.common.annotation.RpcService;
import com.light.rpc.common.bean.RpcRequest;
import com.light.rpc.common.bean.RpcResponse;
import com.light.rpc.common.codec.ProtostuffSerializer;
import com.light.rpc.common.codec.Serializer;
import com.light.rpc.common.util.RpcDecoder;
import com.light.rpc.common.util.RpcEncoder;
import com.light.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC服务端
 */
@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    private String serverAddress;
    private int serverPort;
    private ServiceRegistry serviceRegistry;
    
    // 存放服务名称与服务对象的映射关系
    private Map<String, Object> handlerMap = new HashMap<>();
    
    // 序列化器
    private Serializer serializer = new ProtostuffSerializer();
    
    // Netty相关成员
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * 构造方法
     *
     * @param serverAddress   服务地址
     * @param serviceRegistry 服务注册中心
     */
    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
        
        // 解析服务地址
        String[] addressParts = serverAddress.split(":");
        if (addressParts.length == 2) {
            this.serverAddress = addressParts[0];
            this.serverPort = Integer.parseInt(addressParts[1]);
        } else {
            throw new IllegalArgumentException("服务地址格式错误，正确格式为：host:port");
        }
    }

    /**
     * 自动扫描并注册标注了RpcService注解的服务
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 扫描带有RpcService注解的Bean
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        
        if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                
                // 获取服务名称（接口名+版本号）
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                
                if (StringUtils.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                
                // 添加到服务映射
                handlerMap.put(serviceName, serviceBean);
                
                log.info("加载服务: {}", serviceName);
            }
        }
    }

    /**
     * 启动RPC服务
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动Netty服务器
        startServer();
        
        // 注册所有服务到注册中心
        registerServices();
        
        // 关闭钩子，在JVM关闭前释放资源
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    /**
     * 启动服务器
     */
    private void startServer() {
        // 创建并配置Netty服务器
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        try {
            // 创建并初始化Netty服务端Bootstrap对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 添加解码器
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new RpcDecoder(RpcRequest.class, serializer))
                                    // 添加编码器
                                    .addLast(new RpcEncoder(RpcResponse.class, serializer))
                                    // 添加RPC请求处理器
                                    .addLast(new RpcServerHandler(handlerMap));
                        }
                    });
            
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(serverAddress, serverPort).sync();
            
            log.info("服务器已启动: {}:{}", serverAddress, serverPort);
            
            // 等待服务端监听端口关闭
            // future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("启动服务器失败", e);
            throw new RuntimeException("启动服务器失败", e);
        }
    }

    /**
     * 注册服务到注册中心
     */
    private void registerServices() throws Exception {
        if (serviceRegistry != null && !handlerMap.isEmpty()) {
            // 获取本地IP地址
            // String host = InetAddress.getLocalHost().getHostAddress();
            
            // 注册所有服务
            for (String serviceName : handlerMap.keySet()) {
                // String serviceAddress = host + ":" + serverPort;
                String serviceAddress = serverAddress + ":" + serverPort;
                serviceRegistry.register(serviceName, serviceAddress);
                log.info("注册服务: {} => {}", serviceName, serviceAddress);
            }
        }
    }

    /**
     * 停止RPC服务
     */
    public void stop() {
        // 优雅关闭Netty服务器
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        
        log.info("RPC服务端已停止");
    }

    /**
     * Spring容器销毁时调用
     */
    @Override
    public void destroy() throws Exception {
        stop();
    }
}
