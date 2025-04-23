package com.light.rpc.client.netty;

import com.light.rpc.common.pojo.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * 接收来自服务器的RpcResponse对象，并在处理过程中管理生命周期
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    // 用户读取服务器端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        // 接收到response，给channel设置别名，让sendRequest读取response
        // 将服务器返回的RpcResponse对象放入channel的属性中，以便后续逻辑能通过channel获取该响应数据
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
        ctx.channel().attr(key).set(rpcResponse);
        ctx.channel().close(); // 关闭当前channel（短连接模式）
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}