package com.light.rpc.client;

import com.light.rpc.common.bean.RpcRequest;
import com.light.rpc.common.bean.RpcResponse;
import com.light.rpc.common.codec.ProtostuffSerializer;
import com.light.rpc.common.codec.Serializer;
import com.light.rpc.common.util.RpcDecoder;
import com.light.rpc.common.util.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC客户端
 */
@Slf4j
public class RpcClient {

    private String host;
    private int port;
    private Serializer serializer = new ProtostuffSerializer();
    
    private EventLoopGroup group;
    private RpcClientHandler handler;
    
    private boolean connected = false;

    /**
     * 构造方法
     * 
     * @param serverAddress 服务地址（格式：host:port）
     */
    public RpcClient(String serverAddress) {
        // 解析服务地址
        String[] parts = serverAddress.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("服务地址格式错误，正确格式为：host:port");
        }
        
        this.host = parts[0];
        this.port = Integer.parseInt(parts[1]);
    }

    /**
     * 连接RPC服务器
     */
    public void connect() throws InterruptedException {
        if (connected) {
            return;
        }
        
        try {
            // 创建RPC客户端
            handler = new RpcClientHandler();
            group = new NioEventLoopGroup();
            
            // 创建并初始化Bootstrap对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 添加解码器
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new RpcDecoder(RpcResponse.class, serializer))
                                    // 添加编码器
                                    .addLast(new RpcEncoder(RpcRequest.class, serializer))
                                    // 添加RPC客户端处理器
                                    .addLast(handler);
                        }
                    });
            
            // 连接RPC服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("连接到RPC服务器: {}:{}", host, port);
            
            // 标记为已连接
            connected = true;
            
            // 等待连接关闭（在close方法中调用）
            // future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("连接RPC服务器失败: {}:{}", host, port, e);
            if (group != null) {
                group.shutdownGracefully();
            }
            throw e;
        }
    }

    /**
     * 发送RPC请求
     * 
     * @param request RPC请求
     * @return RPC异步结果
     */
    public RpcClientHandler.RpcFuture send(RpcRequest request) throws Exception {
        if (!connected) {
            connect();
        }
        
        // 发送请求并返回异步结果
        return handler.sendRequest(request);
    }

    /**
     * 关闭RPC客户端
     */
    public void close() {
        if (handler != null) {
            handler.close();
        }
        
        if (group != null) {
            group.shutdownGracefully();
        }
        
        connected = false;
        log.info("RPC客户端已关闭");
    }
}
