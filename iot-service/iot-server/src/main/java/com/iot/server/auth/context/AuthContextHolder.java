package com.iot.server.auth.context;

import com.iot.server.auth.token.TokenPayload;

public final class AuthContextHolder {

    private static final ThreadLocal<TokenPayload> AUTH_HOLDER = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static TokenPayload get() {
        return AUTH_HOLDER.get();
    }

    public static void set(TokenPayload payload) {
        AUTH_HOLDER.set(payload);
    }

    public static void clear() {
        AUTH_HOLDER.remove();
    }
}
