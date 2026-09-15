package com.iot.core.boot.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = "iot.cors")
public class IotCorsProperties {

    // 默认不启用跨域放行
    private boolean enabled = false;

    // 允许访问的前端来源，默认没有任何来源
    private List<String> allowedOrigins = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
