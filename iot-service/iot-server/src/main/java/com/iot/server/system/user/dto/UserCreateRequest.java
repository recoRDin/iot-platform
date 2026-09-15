package com.iot.server.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UserCreateRequest {

    @NotBlank(message = "登录账号不能为空")
    @Size(min = 4, max = 50, message = "登录账号长度必须在4到50个字符之间")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_-]*$",
            message = "登录账号只能以字母开头，并包含字母、数字、下划线或横线")
    private String account;

    @NotBlank(message = "登录密码不能为空")
    @Size(min = 8, max = 64, message = "登录密码长度必须在8到64个字符之间")
    private String password;

    @NotBlank(message = "用户姓名不能为空")
    @Size(max = 50, message = "用户姓名不能超过50个字符")
    private String realName;

    @NotEmpty(message = "用户角色不能为空")
    private List<@NotNull(message = "角色ID不能为空")
            @Positive(message = "角色ID必须大于0") Long> roleIds;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
