USE `iot-platform`;

CREATE TABLE IF NOT EXISTS `iot_product` (
    `id` bigint(20) NOT NULL COMMENT '产品主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `product_key` varchar(32) NOT NULL COMMENT '产品编码',
    `product_name` varchar(50) NOT NULL COMMENT '产品名称',
    `product_desc` varchar(500) DEFAULT NULL COMMENT '产品描述',
    `device_type` varchar(20) NOT NULL COMMENT '设备类型：direct_connect、gateway、gateway_child',
    `link_protocol` varchar(50) NOT NULL COMMENT '连接协议，例如 mqtt、modbus',
    `connect_mode` varchar(50) DEFAULT NULL COMMENT '联网方式，例如 wifi、ethernet、cellular_network、lora',
    `data_type` varchar(16) NOT NULL COMMENT '数据格式：alink_json、custom',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-停用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_tenant_key` (`tenant_id`, `product_key`),
    KEY `idx_product_tenant_status` (`tenant_id`, `status`),
    KEY `idx_product_tenant_name` (`tenant_id`, `product_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT产品表';
