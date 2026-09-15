package com.iot.server.auth.token;

import java.util.List;

public class TokenPayload {

    private final String userId;
    private final String tenantId;
    private final String account;
    private final List<String> roleCodes;
    private final long issuedAt;
    private final long expiresAt;

    public TokenPayload(String userId, String tenantId, String account,
                        List<String> roleCodes, long issuedAt, long expiresAt) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.account = account;
        this.roleCodes = List.copyOf(roleCodes);
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
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

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
