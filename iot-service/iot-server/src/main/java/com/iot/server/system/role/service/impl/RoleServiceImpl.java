package com.iot.server.system.role.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.system.role.dto.RoleCreateRequest;
import com.iot.server.system.role.entity.RoleEntity;
import com.iot.server.system.role.mapper.RoleMapper;
import com.iot.server.system.role.service.IRoleService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity>
        implements IRoleService {

    private static final Pattern ROLE_CODE_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_-]*$");

    @Override
    public Long create(RoleCreateRequest request) {
        if (request == null) {
            throw new ServiceException("角色参数不能为空");
        }

        String roleName = normalize(request.getRoleName(), "角色名称");
        String roleCode = normalize(request.getRoleCode(), "角色编码");

        if (roleName.length() > 50) {
            throw new ServiceException("角色名称不能超过50个字符");
        }
        if (roleCode.length() > 50 || !ROLE_CODE_PATTERN.matcher(roleCode).matches()) {
            throw new ServiceException("角色编码格式不正确");
        }

        RoleEntity role = new RoleEntity();
        role.setTenantId(TenantUtil.getTenantId());
        role.setRoleName(roleName);
        role.setRoleCode(roleCode);
        role.setStatus(1);

        try {
            if (!save(role)) {
                throw new ServiceException("创建角色失败");
            }
            return role.getId();
        } catch (DuplicateKeyException exception) {
            throw new ServiceException("当前租户下角色编码已存在");
        }
    }

    @Override
    public List<String> listEnabledRoleCodes(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return list(Wrappers.<RoleEntity>lambdaQuery()
                .in(RoleEntity::getId, roleIds)
                .eq(RoleEntity::getStatus, 1))
                .stream()
                .map(RoleEntity::getRoleCode)
                .distinct()
                .toList();
    }

    private String normalize(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(fieldName + "不能为空");
        }
        return value.strip();
    }
}
