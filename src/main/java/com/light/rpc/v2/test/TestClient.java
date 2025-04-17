package com.light.rpc.v2.test;

import com.light.rpc.v2.client.proxy.ClientProxy;
import com.light.rpc.v2.common.pojo.User;
import com.light.rpc.v2.common.service.UserService;

public class TestClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务器得到的user：" + user.toString());

        User u = User.builder().id(100).userName("mkh").sex(true).build();
        Integer id = proxy.insertUserId(u);
        System.out.println("向服务器插入的user的id：" + id);
    }
}
