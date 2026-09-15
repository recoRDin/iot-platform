package com.iot.server.log.service;


import com.iot.core.log.model.LogApi;
import com.iot.server.log.entity.ApiLogEntity;
import com.iot.server.log.mapper.ApiLogMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class ApiLogService {

    private final ApiLogMapper apiLogMapper;

    public ApiLogService(ApiLogMapper apiLogMapper) {
        this.apiLogMapper = apiLogMapper;
    }

    public void save(LogApi logApi) {
        Objects.requireNonNull(logApi, "API日志不能为空");

        ApiLogEntity entity = convertToEntity(logApi);

        apiLogMapper.insert(entity);
    }

    private ApiLogEntity convertToEntity(LogApi logApi) {
        ApiLogEntity entity = new ApiLogEntity();

        entity.setTraceId(logApi.getTraceId());
        entity.setTenantId(logApi.getTenantId());
        entity.setServiceId(logApi.getServiceId());
        entity.setServerIp(logApi.getServerIp());
        entity.setServerHost(logApi.getServerHost());
        entity.setEnv(logApi.getEnv());
        entity.setRemoteIp(logApi.getRemoteIp());
        entity.setUserAgent(logApi.getUserAgent());
        entity.setRequestUri(logApi.getRequestUri());
        entity.setMethod(logApi.getMethod());
        entity.setMethodClass(logApi.getMethodClass());
        entity.setMethodName(logApi.getMethodName());
        entity.setParams(logApi.getParams());
        entity.setType(logApi.getType());
        entity.setTitle(logApi.getTitle());
        entity.setTime(parseTime(logApi.getTime()));
        entity.setCreateBy(logApi.getCreateBy());
        entity.setCreateTime(logApi.getCreateTime());

        return entity;
    }

    private Long parseTime(String time) {
        if (time == null || time.isBlank()) {
            return 0L;
        }

        try {
            return Long.valueOf(time);
        } catch (NumberFormatException exception) {
            return 0L;
        }
    }
}
