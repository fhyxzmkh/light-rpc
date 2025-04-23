package com.light.rpc.common.service;

import com.light.rpc.common.pojo.User;

public interface UserService {

    User getUserByUserId(Integer id);

    Integer insertUserId(User user);
}
