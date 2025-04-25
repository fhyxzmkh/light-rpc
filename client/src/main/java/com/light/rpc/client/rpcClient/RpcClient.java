package com.light.rpc.client.rpcClient;

import com.light.rpc.common.pojo.RpcRequest;
import com.light.rpc.common.pojo.RpcResponse;


public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
