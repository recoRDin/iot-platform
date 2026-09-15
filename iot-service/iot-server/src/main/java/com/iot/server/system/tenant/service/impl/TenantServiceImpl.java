package com.iot.server.system.tenant.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantId;
import com.iot.server.system.tenant.dto.TenantCreateRequest;
import com.iot.server.system.tenant.entity.TenantEntity;
import com.iot.server.system.tenant.mapper.TenantMapper;
import com.iot.server.system.tenant.service.ITenantService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TenantServiceImpl implements ITenantService {

    //最大尝试次数
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final TenantMapper tenantMapper;
    private final TenantId tenantIdGenerator;

    public TenantServiceImpl(
            TenantMapper tenantMapper,
            TenantId tenantIdGenerator) {
        this.tenantMapper = tenantMapper;
        this.tenantIdGenerator = tenantIdGenerator;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TenantCreateRequest request){

        if (request == null
                || request.getTenantName() == null
                || request.getTenantName().isBlank()) {
            throw new ServiceException("租户名称不能为空");
        }

        String tenantName = request.getTenantName().strip();

        if (tenantName.length() > 50) {
            throw new ServiceException("租户名称不能超过50个字符");
        }

        for (int attempt = 0;
             attempt < MAX_GENERATION_ATTEMPTS;
             attempt++) {

            // 每次重试重新创建实体，避免沿用失败插入时生成的主键。
            TenantEntity tenant = new TenantEntity();
            tenant.setTenantId(tenantIdGenerator.generate());
            tenant.setTenantName(tenantName);
            tenant.setStatus(1);

            try {
                int rows = tenantMapper.insert(tenant);

                if (rows != 1) {
                    throw new ServiceException("创建租户失败");
                }

                return tenant.getId();
            } catch (DuplicateKeyException exception) {
                // 只有租户编号重复才重新生成。
                // 主键或其他唯一字段冲突，继续抛出原异常。
                if (!isTenantIdConflict(exception)) {
                    throw exception;
                }
            }
        }

        throw new ServiceException("租户编号生成冲突，请稍后重试");
    }

    @Override
    public TenantEntity detail(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException("租户记录主键不正确");
        }

        TenantEntity tenant = tenantMapper.selectById(id);

        if (tenant == null) {
            throw new ServiceException("租户不存在或已删除");
        }

        return tenant;
    }

    @Override
    public TenantEntity findByTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return null;
        }
        return tenantMapper.selectOne(
                Wrappers.<TenantEntity>lambdaQuery()
                        .eq(TenantEntity::getTenantId, tenantId.strip()));
    }

    private boolean isTenantIdConflict(DuplicateKeyException exception) {
        String message = exception.getMostSpecificCause().getMessage();

        if (message == null) {
            return false;
        }

        return message.contains("for key 'uk_tenant_id'")
                || message.contains("for key 'sys_tenant.uk_tenant_id'");
    }
}
