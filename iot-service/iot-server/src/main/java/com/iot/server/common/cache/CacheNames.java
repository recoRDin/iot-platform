package com.iot.server.common.cache;

/**
 * Redis 缓存名称与Key前缀
 */
public interface CacheNames {

    /**
     * 平台统一前缀
     */
    String PLATFORM = "iot:";

    /**
     * 租户数据
     */
    String TENANT = PLATFORM + "tenant:";

    /**
     * 设备数据
     */
    String DEVICE = PLATFORM + "device:";

    /**
     * 产品数据
     */
    String PRODUCT = PLATFORM + "product:";

    /**
     * 告警数据
     */
    String ALARM = PLATFORM + "alarm:";

    /**
     * 幂等标记
     */
    String IDEMPOTENT = PLATFORM + "idempotent:";
}