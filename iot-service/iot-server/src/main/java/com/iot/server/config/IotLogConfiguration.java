package com.iot.server.config;

import com.iot.server.log.listener.ApiLogListener;
import com.iot.server.log.service.ApiLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class IotLogConfiguration {

    @Bean
    public ApiLogListener apiLogListener(ApiLogService apiLogService) {
        return new ApiLogListener(apiLogService);
    }
}
