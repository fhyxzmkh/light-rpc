package com.light.rpc.client;

import com.light.rpc.common.bean.RpcRequest;
import com.light.rpc.common.bean.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * RPC客户端处理器
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel;
    
    // 存放请求ID与响应结果的映射
    private Map<String, RpcFuture> pendingRpcFutures = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        log.info("与服务器建立连接: {}", channel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("与服务器断开连接: {}", channel.remoteAddress());
        // 可以在此处添加重连逻辑
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        log.debug("接收到RPC响应: {}", requestId);
        
        // 根据请求ID获取对应的RPC Future
        RpcFuture rpcFuture = pendingRpcFutures.get(requestId);
        if (rpcFuture != null) {
            // 设置响应结果并释放等待线程
            rpcFuture.setResponse(response);
            pendingRpcFutures.remove(requestId);
        } else {
            log.warn("找不到请求ID的RPC Future: {}", requestId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("RPC客户端异常", cause);
        ctx.close();
    }

    /**
     * 发送RPC请求
     *
     * @param request RPC请求
     * @return RPC异步结果
     */
    public RpcFuture sendRequest(RpcRequest request) {
        RpcFuture rpcFuture = new RpcFuture(request);
        pendingRpcFutures.put(request.getRequestId(), rpcFuture);
        
        // 发送请求
        channel.writeAndFlush(request);
        log.debug("发送RPC请求: {}", request.getRequestId());
        
        return rpcFuture;
    }

    /**
     * 关闭客户端连接
     */
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * 获取RPC异步结果内部类
     */
    public static class RpcFuture {
        private RpcRequest request;
        private RpcResponse response;
        private CountDownLatch latch = new CountDownLatch(1);
        private long startTime;

        public RpcFuture(RpcRequest request) {
            this.request = request;
            this.startTime = System.currentTimeMillis();
        }

        /**
         * 等待并获取RPC结果
         */
        public Object get(long timeout) throws Exception {
            // 等待RPC响应或超时
            latch.await(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            if (response == null) {
                throw new RuntimeException("RPC调用超时: " + request.getInterfaceName() + "." + request.getMethodName());
            }
            
            if (!response.isSuccess()) {
                throw new RuntimeException("RPC调用失败: " + response.getError());
            }
            
            return response.getResult();
        }

        /**
         * 设置RPC响应结果
         */
        private void setResponse(RpcResponse response) {
            this.response = response;
            latch.countDown();
            long costTime = System.currentTimeMillis() - startTime;
            
            if (log.isDebugEnabled()) {
                log.debug("RPC调用完成，耗时: {}ms", costTime);
            }
        }
    }
}
