package com.iot.core.tenant.config;


//多租户自动配置

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.iot.core.tenant.IotTenantId;
import com.iot.core.tenant.TenantId;
import com.iot.core.tenant.handler.IotTenantLineHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(TenantProperties.class)
public class IotTenantAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TenantLineHandler.class)
    public TenantLineHandler tenantLineHandler(TenantProperties tenantProperties) {

        return new IotTenantLineHandler(tenantProperties);
    }

    @Bean
    @ConditionalOnMissingBean(TenantId.class)
    public TenantId tenantId() {
        return new IotTenantId();
    }
}
