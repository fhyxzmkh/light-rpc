package com.light.rpc.server.rpcServer.impl;

import com.light.rpc.server.config.ServerConfig;
import com.light.rpc.server.netty.NettyServerInitializer;
import com.light.rpc.server.provider.ServiceProvider;
import com.light.rpc.server.rpcServer.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyRpcServer implements RpcServer {

    @Resource
    private ServiceProvider serviceProvider;
    
    @Resource
    private ServerConfig serverConfig;
    
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;
    
    @PostConstruct
    public void init() {
        if (serverConfig.isAutoStart()) {
            start();
        }
    }

    @Override
    public void start() {
        // netty 服务线程组boss负责建立连接， work负责具体的请求
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        log.info("Netty服务端启动，端口: {}", serverConfig.getPort());
        
        try {
            //启动netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //初始化
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceProvider));
                    
            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(serverConfig.getPort()).sync();
            
            // 不阻塞主线程
            new Thread(() -> {
                try {
                    //死循环监听
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("服务器监听异常", e);
                } finally {
                    // 优雅关闭
                    bossGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                }
            }).start();
            
        } catch (InterruptedException e) {
            log.error("服务器启动异常", e);
            stop();
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        log.info("Netty服务器关闭");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }
    
    @Override
    public boolean isRunning() {
        return bossGroup != null && !bossGroup.isShutdown() && !bossGroup.isShuttingDown()
                && workGroup != null && !workGroup.isShutdown() && !workGroup.isShuttingDown();
    }
}