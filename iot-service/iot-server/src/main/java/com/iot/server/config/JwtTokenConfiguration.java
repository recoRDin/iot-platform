package com.iot.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.server.auth.filter.JwtAuthenticationFilter;
import com.iot.server.auth.token.JwtTokenService;
import com.iot.server.auth.token.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TokenProperties.class)
public class JwtTokenConfiguration {

    @Bean
    public JwtTokenService jwtTokenService(TokenProperties properties) {
        return new JwtTokenService(properties);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
            JwtTokenService jwtTokenService,
            ObjectMapper objectMapper,
            Environment environment) {
        boolean devMode = environment.acceptsProfiles(Profiles.of("dev"));

        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new JwtAuthenticationFilter(jwtTokenService, objectMapper, devMode));

        registration.addUrlPatterns("/*");
        registration.setName("jwtAuthenticationFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return registration;
    }
}
