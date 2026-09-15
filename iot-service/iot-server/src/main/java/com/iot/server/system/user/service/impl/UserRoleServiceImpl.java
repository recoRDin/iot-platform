package com.iot.server.system.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.system.role.entity.RoleEntity;
import com.iot.server.system.role.mapper.RoleMapper;
import com.iot.server.system.user.entity.UserRoleEntity;
import com.iot.server.system.user.mapper.UserRoleMapper;
import com.iot.server.system.user.mapper.UserMapper;
import com.iot.server.system.user.service.IUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity>
        implements IUserRoleService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public UserRoleServiceImpl(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        //校验用户和角色
        if (userId == null || userId <= 0) {
            throw new ServiceException("用户ID不正确");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            throw new ServiceException("角色不能为空");
        }
        //写入角色
        Set<Long> uniqueRoleIds = new LinkedHashSet<>();
        for (Long roleId : roleIds) {
            if (roleId == null || roleId <= 0) {
                throw new ServiceException("角色ID不正确");
            }
            uniqueRoleIds.add(roleId);
        }

        if (userMapper.selectById(userId) == null) {
            throw new ServiceException("用户不存在或已删除");
        }

        Long roleCount = roleMapper.selectCount(
                Wrappers.<RoleEntity>lambdaQuery()
                        .in(RoleEntity::getId, uniqueRoleIds));
        if (roleCount != uniqueRoleIds.size()) {
            throw new ServiceException("角色不存在或已删除");
        }
        //删除用户原角色关系
        remove(Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getUserId, userId));
        //写入新角色关系
        String tenantId = TenantUtil.getTenantId();
        List<UserRoleEntity> relations = uniqueRoleIds.stream()
                .map(roleId -> createRelation(tenantId, userId, roleId))
                .toList();

        if (!saveBatch(relations)) {
            throw new ServiceException("分配用户角色失败");
        }
    }

    @Override
    public List<Long> listRoleIds(Long userId) {
        if (userId == null || userId <= 0) {
            return List.of();
        }
        return list(Wrappers.<UserRoleEntity>lambdaQuery()
                .eq(UserRoleEntity::getUserId, userId))
                .stream()
                .map(UserRoleEntity::getRoleId)
                .toList();
    }

    private UserRoleEntity createRelation(String tenantId, Long userId, Long roleId) {
        UserRoleEntity relation = new UserRoleEntity();
        relation.setTenantId(tenantId);
        relation.setUserId(userId);
        relation.setRoleId(roleId);
        return relation;
    }
}
