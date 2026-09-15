package com.iot.core.tenant.constant;

public class TenantConstants {

    //默认管理租户
    public static final String DEFAULT_TENANT_ID = "000000";

    //请求头中的租户
    public static final String TENANT_HEADER = "Tenant-Id";

    //数据库租户字段
    public static final String TENANT_COLUMN = "tenant_id";

    private TenantConstants() {}
}
