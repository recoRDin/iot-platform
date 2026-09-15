package com.iot.server.auth.model;

import java.util.List;

public class AuthenticatedUser {

    private final String userId;
    private final String tenantId;
    private final String account;
    private final String realName;
    private final List<String> roleCodes;

    public AuthenticatedUser(String userId, String tenantId, String account,
                             String realName, List<String> roleCodes) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.account = account;
        this.realName = realName;
        this.roleCodes = List.copyOf(roleCodes);
    }

    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getAccount() {
        return account;
    }

    public String getRealName() {
        return realName;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }
}
