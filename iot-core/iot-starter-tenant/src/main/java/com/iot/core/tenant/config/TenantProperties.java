package com.iot.core.tenant.config;

import com.iot.core.tenant.constant.TenantConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 多租户配置。
 */
@ConfigurationProperties(prefix = "iot.tenant")
public class TenantProperties {

    /**
     * 是否启用租户隔离。
     */
    private boolean enabled = true;

    /**
     * 数据库租户字段名称。
     */
    private String column = TenantConstants.TENANT_COLUMN;

    /**
     * 需要自动进行租户隔离的表。
     */
    private List<String> tables = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}