package com.iot.server.system.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.server.system.tenant.entity.TenantEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantMapper extends BaseMapper<TenantEntity> {
}
