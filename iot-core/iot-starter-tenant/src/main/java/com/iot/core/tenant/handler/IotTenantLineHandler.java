package com.iot.core.tenant.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.iot.core.tenant.TenantUtil;
import com.iot.core.tenant.config.TenantProperties;
import com.iot.core.tenant.context.TenantContextHolder;
import net.sf.jsqlparser.expression.StringValue;

import net.sf.jsqlparser.expression.Expression;


//租户处理器
public class IotTenantLineHandler implements TenantLineHandler {

    private final TenantProperties tenantProperties;

    public IotTenantLineHandler(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
    }


    //返回当前租户对应的SQL字符串
    @Override
    public Expression getTenantId(){

        String tenantId = TenantContextHolder.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("访问租户数据前必须设置租户上下文");
        }

        // 限制业务租户号的长度和字符，保留前导零。
        if (!tenantId.matches("[A-Za-z0-9_-]{1,12}")) {
            throw new IllegalArgumentException("租户ID格式不正确");
        }

        return new StringValue(tenantId);
    }

    //返回数据库中的租户字段名
    @Override
    public String getTenantIdColumn() {
        return tenantProperties.getColumn();
    }

    //跳过租户隔离
    @Override
    public boolean ignoreTable(String tableName) {
        // 总开关关闭
        if (!tenantProperties.isEnabled()) {
            return true;
        }

        // 当前代码通过 TenantUtil.ignore() 临时放行。
        if (TenantUtil.isIgnore()) {
            return true;
        }

        // 兼容 MySQL 表名中的反引号。
        String actualTableName = tableName.replace("`", "");

        for (String configuredTable : tenantProperties.getTables()) {
            if (actualTableName.equalsIgnoreCase(configuredTable.trim())) {
                return false;
            }
        }

        // 没有配置的表不进行自动隔离。
        return true;
    }
}
