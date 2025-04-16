package com.light.rpc.v1.common.service;

import com.light.rpc.v1.common.pojo.User;

public interface UserService {

    User getUserByUserId(Integer id);

    Integer insertUserId(User user);
}
