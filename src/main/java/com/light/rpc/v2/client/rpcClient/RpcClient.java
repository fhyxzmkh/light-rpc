package com.light.rpc.v2.client.rpcClient;

import com.light.rpc.v2.common.pojo.RpcRequest;
import com.light.rpc.v2.common.pojo.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
