USE `iot-platform`;

CREATE TABLE IF NOT EXISTS `iot_product_model_version` (
    `id` bigint(20) NOT NULL COMMENT '物模型版本主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `product_id` bigint(20) NOT NULL COMMENT '所属产品主键',
    `product_key` varchar(32) NOT NULL COMMENT '产品编码快照',
    `version_no` int(11) NOT NULL COMMENT '产品内递增版本号',
    `version_desc` varchar(200) DEFAULT NULL COMMENT '版本说明',
    `data_status` varchar(16) NOT NULL COMMENT '版本状态：published、history',
    `snapshot_json` longtext NOT NULL COMMENT '产品及属性、事件、服务完整JSON快照',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_version_tenant_product_no`
        (`tenant_id`, `product_id`, `version_no`),
    KEY `idx_model_version_tenant_product_status`
        (`tenant_id`, `product_id`, `data_status`, `version_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT产品物模型发布版本表';
