package com.light.rpc.server;

import com.light.rpc.common.bean.RpcRequest;
import com.light.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC服务处理器
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 创建并初始化RPC响应对象
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        
        try {
            // 处理RPC请求
            Object result = handle(request);
            response.setResult(result);
        } catch (Exception e) {
            log.error("处理RPC请求失败", e);
            response.setError(e.getMessage());
        }
        
        // 写入响应
        ctx.writeAndFlush(response);
    }

    /**
     * 处理RPC请求
     */
    private Object handle(RpcRequest request) throws Exception {
        // 获取服务对象
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getVersion();
        
        if (serviceVersion != null && !serviceVersion.isEmpty()) {
            serviceName += "-" + serviceVersion;
        }
        
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException("找不到服务: " + serviceName);
        }
        
        // 获取方法调用参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        
        // 使用标准Java反射调用方法（替代CGLIB FastClass）
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        
        log.debug("调用服务方法: {}.{}", serviceClass.getName(), methodName);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务器异常", cause);
        ctx.close();
    }
}
