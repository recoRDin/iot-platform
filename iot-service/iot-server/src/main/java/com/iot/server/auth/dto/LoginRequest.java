package com.iot.server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "租户编号不能为空")
    @Size(max = 12, message = "租户编号不能超过12个字符")
    private String tenantId;

    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号不能超过50个字符")
    private String account;

    @NotBlank(message = "登录密码不能为空")
    @Size(max = 64, message = "登录密码不能超过64个字符")
    private String password;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
