CREATE DATABASE IF NOT EXISTS `iot-platform`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `iot-platform`;

CREATE TABLE IF NOT EXISTS `sys_tenant` (
    `id` bigint(20) NOT NULL COMMENT '数据库主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '业务租户编号',
    `tenant_name` varchar(100) NOT NULL COMMENT '租户名称',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_id` (`tenant_id`),
    KEY `idx_tenant_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台租户表';

CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` bigint(20) NOT NULL COMMENT '数据库主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_tenant_code` (`tenant_id`, `role_code`),
    KEY `idx_role_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` bigint(20) NOT NULL COMMENT '数据库主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `account` varchar(50) NOT NULL COMMENT '登录账号',
    `password` varchar(255) NOT NULL COMMENT '密码哈希',
    `real_name` varchar(50) NOT NULL COMMENT '用户姓名',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tenant_account` (`tenant_id`, `account`),
    KEY `idx_user_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` bigint(20) NOT NULL COMMENT '数据库主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `user_id` bigint(20) NOT NULL COMMENT '用户主键',
    `role_id` bigint(20) NOT NULL COMMENT '角色主键',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`tenant_id`, `user_id`, `role_id`),
    KEY `idx_user_role_user` (`tenant_id`, `user_id`),
    KEY `idx_user_role_role` (`tenant_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS `iot_log_api` (
    `id` bigint(20) NOT NULL COMMENT '主键ID',
    `trace_id` varchar(64) DEFAULT NULL COMMENT '链路追踪ID',
    `tenant_id` varchar(64) DEFAULT NULL COMMENT '租户ID',
    `service_id` varchar(64) DEFAULT NULL COMMENT '服务名称',
    `server_ip` varchar(64) DEFAULT NULL COMMENT '服务端IP',
    `server_host` varchar(255) DEFAULT NULL COMMENT '服务端主机名',
    `env` varchar(32) DEFAULT NULL COMMENT '运行环境',
    `remote_ip` varchar(64) DEFAULT NULL COMMENT '客户端IP',
    `user_agent` varchar(512) DEFAULT NULL COMMENT '客户端User-Agent',
    `request_uri` varchar(1024) DEFAULT NULL COMMENT '请求路径',
    `method` varchar(16) DEFAULT NULL COMMENT 'HTTP请求方法',
    `method_class` varchar(255) DEFAULT NULL COMMENT 'Java类名',
    `method_name` varchar(128) DEFAULT NULL COMMENT 'Java方法名',
    `params` text COMMENT '请求参数',
    `type` char(1) NOT NULL DEFAULT '1' COMMENT '日志类型：1-API操作日志',
    `title` varchar(128) DEFAULT NULL COMMENT '操作标题',
    `time` bigint(20) NOT NULL DEFAULT '0' COMMENT '执行耗时，单位毫秒',
    `create_by` varchar(64) DEFAULT NULL COMMENT '操作人ID',
    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_log_api_trace_id` (`trace_id`),
    KEY `idx_log_api_create_time` (`create_time`),
    KEY `idx_log_api_tenant_time` (`tenant_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API操作日志';

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

CREATE TABLE IF NOT EXISTS `iot_product_property` (
    `id` bigint(20) NOT NULL COMMENT '属性定义主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `product_id` bigint(20) NOT NULL COMMENT '所属产品主键',
    `identifier` varchar(64) NOT NULL COMMENT '属性标识符，例如 waterLevel',
    `property_name` varchar(100) NOT NULL COMMENT '属性名称，例如当前水位',
    `access_mode` varchar(2) NOT NULL DEFAULT 'r' COMMENT '访问方式：r-只读，rw-读写',
    `data_type` varchar(16) NOT NULL COMMENT '数据类型：int32、float、double、text、bool、enum、date',
    `spec_json` json NULL COMMENT '数据规格，例如范围、步长、单位、布尔文本或枚举项',
    `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必需：0-否，1-是',
    `description` varchar(500) DEFAULT NULL COMMENT '属性说明',
    `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序号',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-停用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_property_tenant_product_identifier` (`tenant_id`, `product_id`, `identifier`),
    KEY `idx_property_tenant_product_status` (`tenant_id`, `product_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT产品物模型属性定义表';

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

CREATE TABLE IF NOT EXISTS `iot_product_service` (
    `id` bigint(20) NOT NULL COMMENT '服务定义主键',
    `tenant_id` varchar(12) NOT NULL COMMENT '租户编号',
    `product_id` bigint(20) NOT NULL COMMENT '所属产品主键',
    `identifier` varchar(64) NOT NULL COMMENT '服务标识符，例如 resetDevice',
    `service_name` varchar(100) NOT NULL COMMENT '服务名称，例如设备复位',
    `call_type` varchar(16) NOT NULL COMMENT '调用方式：sync、async',
    `input_json` json NOT NULL COMMENT '服务输入参数定义数组',
    `output_json` json NOT NULL COMMENT '服务输出参数定义数组',
    `required` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否必需：0-否，1-是',
    `description` varchar(500) DEFAULT NULL COMMENT '服务说明',
    `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序号',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-停用，1-启用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_service_tenant_product_identifier` (`tenant_id`, `product_id`, `identifier`),
    KEY `idx_service_tenant_product_status` (`tenant_id`, `product_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT产品物模型服务定义表';

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
