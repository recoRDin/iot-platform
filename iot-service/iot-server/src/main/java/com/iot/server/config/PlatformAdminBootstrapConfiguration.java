package com.iot.server.config;

import com.iot.server.system.bootstrap.PlatformAdminBootstrapProperties;
import com.iot.server.system.bootstrap.PlatformAdminInitializer;
import com.iot.server.system.role.mapper.RoleMapper;
import com.iot.server.system.tenant.mapper.TenantMapper;
import com.iot.server.system.user.mapper.UserMapper;
import com.iot.server.system.user.mapper.UserRoleMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PlatformAdminBootstrapProperties.class)
public class PlatformAdminBootstrapConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "iot.bootstrap.admin",
            name = "enabled",
            havingValue = "true")
    public PlatformAdminInitializer platformAdminInitializer(
            PlatformAdminBootstrapProperties properties,
            TenantMapper tenantMapper,
            RoleMapper roleMapper,
            UserMapper userMapper,
            UserRoleMapper userRoleMapper,
            PasswordEncoder passwordEncoder) {
        return new PlatformAdminInitializer(
                properties,
                tenantMapper,
                roleMapper,
                userMapper,
                userRoleMapper,
                passwordEncoder);
    }
}
