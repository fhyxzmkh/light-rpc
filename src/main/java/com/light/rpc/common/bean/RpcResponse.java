package com.light.rpc.common.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * RPC响应实体类
 */
@Data
public class RpcResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 返回结果
     */
    private Object result;
    
    /**
     * 是否成功
     * @return 如果没有错误信息，则认为是成功的
     */
    public boolean isSuccess() {
        return error == null;
    }
}
