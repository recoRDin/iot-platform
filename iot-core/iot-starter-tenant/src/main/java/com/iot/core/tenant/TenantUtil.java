package com.iot.core.tenant;


import com.iot.core.tenant.constant.TenantConstants;
import com.iot.core.tenant.context.TenantContextHolder;

import java.util.function.Supplier;

//租户上下文工具
public class TenantUtil {

    private TenantUtil() {}

    //获取当前租户，没有租户返沪管理租户
    public static String getTenantId() {

        String tenantId = TenantContextHolder.getTenantId();

        if (tenantId == null  || tenantId.isBlank()) {
            return TenantConstants.DEFAULT_TENANT_ID;
        }
        return tenantId;
    }

    //指定租户上下文中执行无返回值的逻辑
    public static void use(String tenantId,Runnable runnable) {

        validateTenantId(tenantId);

        String previousTenantId = TenantContextHolder.getTenantId();

        try{
            TenantContextHolder.setTenantId(tenantId);
            runnable.run();
        }finally {
            restoreTenantId(previousTenantId);
        }

    }

    //指定租户上下文执行有返回的逻辑
    public static <T>T use(String tenantId, Supplier<T> supplier) {

        validateTenantId(tenantId);

        String previousTenantId = TenantContextHolder.getTenantId();

        try {
            TenantContextHolder.setTenantId(tenantId);
            return supplier.get();
        } finally {
            restoreTenantId(previousTenantId);
        }
    }

    //临时忽略租户隔离
    public static <T> T ignore(Supplier<T> supplier) {
        boolean previousIgnore = TenantContextHolder.isIgnore();

        try {
            TenantContextHolder.setIgnore(true);
            return supplier.get();
        } finally {
            TenantContextHolder.setIgnore(previousIgnore);
        }
    }

    public static void ignore(Runnable runnable) {
        boolean previousIgnore = TenantContextHolder.isIgnore();

        try {
            TenantContextHolder.setIgnore(true);
            runnable.run();
        } finally {
            TenantContextHolder.setIgnore(previousIgnore);
        }
    }

    public static boolean isIgnore() {
        return TenantContextHolder.isIgnore();
    }

    public static void clear() {
        TenantContextHolder.clear();
    }


    private static void validateTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId 不能为空");
        }
    }

    private static void restoreTenantId(String previousTenantId) {
        if (previousTenantId == null || previousTenantId.isBlank()) {
            TenantContextHolder.clearTenantId();
        } else {
            TenantContextHolder.setTenantId(previousTenantId);
        }
    }
}
