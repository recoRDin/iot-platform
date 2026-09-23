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
