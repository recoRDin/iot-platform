package com.iot.server.system.tenant.service;

import com.iot.server.system.tenant.dto.TenantCreateRequest;
import com.iot.server.system.tenant.entity.TenantEntity;

public interface ITenantService {

    //创建租户
    Long create(TenantCreateRequest Request);

    //根据数据库主键id查询
    TenantEntity detail(Long id);

    //根据业务租户编号查询
    TenantEntity findByTenantId(String tenantId);
}
