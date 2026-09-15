package com.iot.server.system.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iot.core.mybatis.base.BaseEntity;

@TableName("sys_tenant")
public class TenantEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 业务租户ID，例如：000000。
     */
    private String tenantId;

    /**
     * 租户名称。
     */
    private String tenantName;

    /**
     * 状态：0-禁用，1-启用。
     */
    private Integer status;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}