package com.iot.core.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.iot.core.mybatis.handler.IotMetaObjectHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

//mybatis配置
@AutoConfiguration(
        before = MybatisPlusAutoConfiguration.class,
        afterName = "com.iot.core.tenant.config.IotTenantAutoConfiguration"
)
@ConditionalOnClass(MybatisPlusInterceptor.class)
public class IotMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<TenantLineHandler> tenantLineHandlerProvider) {

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //  获取容器中的租户处理器。
        TenantLineHandler tenantLineHandler = tenantLineHandlerProvider.getIfAvailable();

        // 引入租户组件后，将处理器交给租户拦截器。
        if (tenantLineHandler != null) {

            TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor(tenantLineHandler);

            interceptor.addInnerInterceptor(tenantInterceptor);
        }

        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);

        // 超出最大页码时返回空数据，不自动跳回第一页
        paginationInterceptor.setOverflow(false);

        // 每页最多查询500条，避免一次查询过多数据
        paginationInterceptor.setMaxLimit(500L);

        //添加分页拦截器
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 防止无条件全表更新或删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {

        //自动填充器
        return new IotMetaObjectHandler();
    }
}
