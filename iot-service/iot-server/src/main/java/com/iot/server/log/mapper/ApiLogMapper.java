package com.iot.server.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.server.log.entity.ApiLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiLogMapper extends BaseMapper<ApiLogEntity> {
}
