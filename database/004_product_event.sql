USE `iot-platform`;

CREATE TABLE IF NOT EXISTS `iot_product_event` (
    `id` bigint(20) NOT NULL COMMENT '事件定义主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `product_id` bigint(20) NOT NULL COMMENT '所属产品主键',
    `identifier` varchar(64) NOT NULL COMMENT '事件标识符，例如 waterLevelAlarm',
    `event_name` varchar(100) NOT NULL COMMENT '事件名称，例如水位超限告警',
    `event_type` varchar(16) NOT NULL COMMENT '事件类型：info、alert、error',
    `output_json` json NOT NULL COMMENT '事件输出参数定义数组',
    `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必需：0-否，1-是',
    `description` varchar(500) DEFAULT NULL COMMENT '事件说明',
    `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序号',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-停用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_event_tenant_product_identifier` (`tenant_id`, `product_id`, `identifier`),
    KEY `idx_event_tenant_product_status` (`tenant_id`, `product_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT产品物模型事件定义表';
