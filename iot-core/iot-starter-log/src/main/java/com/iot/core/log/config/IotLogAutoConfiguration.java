package com.iot.core.log.config;

import com.iot.core.log.aspect.ApiLogAspect;
import com.iot.core.log.error.RestExceptionTranslator;
import com.iot.core.log.filter.TraceIdFilter;
import com.iot.core.log.publisher.ApiLogPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * 在 Servlet Web 应用中自动注册异常处理器。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class IotLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RestExceptionTranslator.class)
    public RestExceptionTranslator restExceptionTranslator() {
        return new RestExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean(ApiLogPublisher.class)
    public ApiLogPublisher apiLogPublisher(ApplicationEventPublisher eventPublisher) {

        return new ApiLogPublisher(eventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean(ApiLogAspect.class)
    public ApiLogAspect apiLogAspect(ApiLogPublisher apiLogPublisher) {

        return new ApiLogAspect(apiLogPublisher);

    }

    @Bean
    @ConditionalOnMissingBean(name = "traceIdFilterRegistration")
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {

        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new TraceIdFilter());

        //拦截所有请求
        registration.addUrlPatterns("/*");

        //设置Servlet Filter名称
        registration.setName("traceIdFilter");

        //先执行，保证后续日志都能获得traceId
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }
}
