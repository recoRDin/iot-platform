package com.iot.server.system.user.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.system.user.entity.UserRoleEntity;

import java.util.List;

public interface IUserRoleService extends IService<UserRoleEntity> {

    void assignRoles(Long userId, List<Long> roleIds);

    List<Long> listRoleIds(Long userId);
}
