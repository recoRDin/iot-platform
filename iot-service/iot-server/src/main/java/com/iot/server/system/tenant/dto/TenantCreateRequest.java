package com.iot.server.system.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TenantCreateRequest {

    /**
     * 租户名称。
     * tenantId 由后端自动生成，不允许前端传入。
     */
    @NotBlank(message = "租户名称不能为空")
    @Size(max = 50, message = "租户名称不能超过50个字符")
    private String tenantName;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}