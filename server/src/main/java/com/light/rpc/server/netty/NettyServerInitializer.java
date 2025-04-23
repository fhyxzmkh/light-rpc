package com.light.rpc.server.netty;

import com.light.rpc.server.provider.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 初始化，每个SocketChannel都会有一个独立的管道，用于定义数据的处理流程
        ChannelPipeline pipeline = socketChannel.pipeline();

        // 消息格式 【长度】【消息体】，解决粘包问题
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE,
                        0,
                        4,
                        0,
                        4
                )
        );

        //计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        // 编解码器
        // 使用Java序列化方式，netty自带的编解码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(className -> Class.forName(className)));

        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }

}
