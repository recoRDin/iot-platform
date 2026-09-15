package com.iot.server.auth.context;

import com.iot.server.auth.token.TokenPayload;

import java.util.List;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static boolean isAuthenticated() {
        return AuthContextHolder.get() != null;
    }

    public static String getUserId() {
        return current().getUserId();
    }

    public static String getTenantId() {
        return current().getTenantId();
    }

    public static String getAccount() {
        return current().getAccount();
    }

    public static List<String> getRoleCodes() {
        return current().getRoleCodes();
    }

    public static boolean hasRole(String roleCode) {
        return roleCode != null && getRoleCodes().contains(roleCode);
    }

    private static TokenPayload current() {
        TokenPayload payload = AuthContextHolder.get();
        if (payload == null) {
            throw new IllegalStateException("当前请求未认证");
        }
        return payload;
    }
}
