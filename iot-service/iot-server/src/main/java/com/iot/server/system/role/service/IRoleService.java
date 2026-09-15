package com.iot.server.system.role.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.system.role.dto.RoleCreateRequest;
import com.iot.server.system.role.entity.RoleEntity;

import java.util.List;

public interface IRoleService extends IService<RoleEntity> {

    Long create(RoleCreateRequest request);

    List<String> listEnabledRoleCodes(List<Long> roleIds);
}
