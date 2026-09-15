package com.iot.server.system.role.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iot.core.mybatis.base.BaseEntity;

@TableName("sys_role")
public class RoleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String tenantId;

    private String roleName;

    private String roleCode;

    /**
     * 状态：0禁用，1启用。
     */
    private Integer status;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}