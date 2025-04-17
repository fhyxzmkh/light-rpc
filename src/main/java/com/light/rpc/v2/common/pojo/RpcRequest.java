package com.light.rpc.v2.common.pojo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcRequest implements Serializable {
    private String interfaceName;

    private String methodName;

    private Object[] params;

    private Class<?>[] paramsType;
}
