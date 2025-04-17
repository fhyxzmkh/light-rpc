package com.light.rpc.v2.common.service;

import com.light.rpc.v2.common.pojo.User;

public interface UserService {

    User getUserByUserId(Integer id);

    Integer insertUserId(User user);
}
