package com.iot.core.tenant.context;

/**
 * 当前线程的租户上下文。
 */
public final class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_ID_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> IGNORE_HOLDER = new ThreadLocal<>();

    private TenantContextHolder() {}

    public static String getTenantId() {
        return TENANT_ID_HOLDER.get();
    }

    public static void setTenantId(String tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    public static void clearTenantId() {
        TENANT_ID_HOLDER.remove();
    }

    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE_HOLDER.get());
    }

    public static void setIgnore(boolean ignore) {
        if (ignore) {
            IGNORE_HOLDER.set(Boolean.TRUE);
        } else {
            IGNORE_HOLDER.remove();
        }
    }

    public static void clear() {
        TENANT_ID_HOLDER.remove();
        IGNORE_HOLDER.remove();
    }
}