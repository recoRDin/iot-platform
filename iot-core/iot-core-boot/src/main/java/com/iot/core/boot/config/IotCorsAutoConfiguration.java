package com.iot.core.boot.config;


import com.iot.core.boot.props.IotCorsProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration(before = WebMvcAutoConfiguration.class)
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "iot.cors",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(IotCorsProperties.class)
public class IotCorsAutoConfiguration implements WebMvcConfigurer {

    private final IotCorsProperties properties;

    public IotCorsAutoConfiguration(IotCorsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry){
        // 启用跨域时，必须明确配置允许的来源
        Assert.notEmpty(
                properties.getAllowedOrigins(),
                "启用跨域时，iot.cors.allowed-origins 不能为空"
        );

        for (String origin : properties.getAllowedOrigins()) {
            Assert.hasText(origin, "跨域来源不能为空字符串");
            Assert.isTrue(
                    !origin.contains("*")
                            && !"null".equalsIgnoreCase(origin.trim()),
                    "跨域来源必须是明确地址，不允许使用通配符或 null"
            );
        }

        registry.addMapping("/**")
                .allowedOrigins(
                        properties.getAllowedOrigins().toArray(new String[0])
                )
                .allowedMethods(
                        "GET", "HEAD", "POST", "PUT",
                        "PATCH", "DELETE", "OPTIONS"
                )
                .allowedHeaders(
                        "Content-Type", "Authorization", "Accept"
                )
                .allowCredentials(false)
                .maxAge(1800);
    }
}
