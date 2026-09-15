package com.iot.server.system.user.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.system.user.dto.UserCreateRequest;
import com.iot.server.system.user.entity.UserEntity;

public interface IUserService extends IService<UserEntity> {

    //创建用户
    Long create(UserCreateRequest request);
    //根据账户查询
    UserEntity findByAccount(String account);
}
