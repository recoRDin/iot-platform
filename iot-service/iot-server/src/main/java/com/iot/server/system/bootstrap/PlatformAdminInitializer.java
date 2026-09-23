package com.iot.server.system.bootstrap;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iot.core.tenant.TenantUtil;
import com.iot.core.tenant.constant.TenantConstants;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.system.role.entity.RoleEntity;
import com.iot.server.system.role.mapper.RoleMapper;
import com.iot.server.system.tenant.entity.TenantEntity;
import com.iot.server.system.tenant.mapper.TenantMapper;
import com.iot.server.system.user.entity.UserEntity;
import com.iot.server.system.user.entity.UserRoleEntity;
import com.iot.server.system.user.mapper.UserMapper;
import com.iot.server.system.user.mapper.UserRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

public class PlatformAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatformAdminInitializer.class);

    private final PlatformAdminBootstrapProperties properties;
    private final TenantMapper tenantMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public PlatformAdminInitializer(PlatformAdminBootstrapProperties properties,
                                    TenantMapper tenantMapper,
                                    RoleMapper roleMapper,
                                    UserMapper userMapper,
                                    UserRoleMapper userRoleMapper,
                                    PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.tenantMapper = tenantMapper;
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        TenantEntity tenant = ensurePlatformTenant();

        TenantUtil.use(TenantConstants.DEFAULT_TENANT_ID, () -> {
            RoleEntity role = ensureAdminRole();
            UserEntity user = ensureAdminUser();
            ensureUserRole(user.getId(), role.getId());
        });

        log.info("平台管理员初始化完成，tenantId：{}，tenantName：{}，account：{}",
                tenant.getTenantId(), tenant.getTenantName(), normalizeAccount());
    }

    private TenantEntity ensurePlatformTenant() {
        TenantEntity tenant = tenantMapper.selectOne(
                Wrappers.<TenantEntity>lambdaQuery()
                        .eq(TenantEntity::getTenantId, TenantConstants.DEFAULT_TENANT_ID));

        if (tenant != null) {
            return tenant;
        }

        tenant = new TenantEntity();
        tenant.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        tenant.setTenantName(normalizeRequired(properties.getTenantName(), "平台租户名称"));
        tenant.setStatus(1);
        tenantMapper.insert(tenant);
        return tenant;
    }

    private RoleEntity ensureAdminRole() {
        RoleEntity role = roleMapper.selectOne(
                Wrappers.<RoleEntity>lambdaQuery()
                        .eq(RoleEntity::getRoleCode, RoleConstants.ADMIN));

        if (role != null) {
            return role;
        }

        role = new RoleEntity();
        role.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        role.setRoleName("平台管理员");
        role.setRoleCode(RoleConstants.ADMIN);
        role.setStatus(1);
        roleMapper.insert(role);
        return role;
    }

    private UserEntity ensureAdminUser() {
        String account = normalizeAccount();
        UserEntity user = userMapper.selectOne(
                Wrappers.<UserEntity>lambdaQuery()
                        .eq(UserEntity::getAccount, account));

        if (user != null) {
            return user;
        }

        String password = validatePassword(properties.getPassword());

        user = new UserEntity();
        user.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(normalizeRequired(properties.getRealName(), "管理员姓名"));
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    private void ensureUserRole(Long userId, Long roleId) {
        Long count = userRoleMapper.selectCount(
                Wrappers.<UserRoleEntity>lambdaQuery()
                        .eq(UserRoleEntity::getUserId, userId)
                        .eq(UserRoleEntity::getRoleId, roleId));

        if (count > 0) {
            return;
        }

        UserRoleEntity relation = new UserRoleEntity();
        relation.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        relation.setUserId(userId);
        relation.setRoleId(roleId);
        userRoleMapper.insert(relation);
    }

    private String normalizeAccount() {
        String account = normalizeRequired(properties.getAccount(), "管理员账号");
        if (!account.matches("^[A-Za-z][A-Za-z0-9_-]{3,49}$")) {
            throw new IllegalStateException("初始化管理员账号格式不正确");
        }
        return account;
    }

    private String validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 64
                || password.isBlank()) {
            throw new IllegalStateException("IOT_BOOTSTRAP_ADMIN_PASSWORD 长度必须在8到64个字符之间");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("IOT_BOOTSTRAP_ADMIN_PASSWORD 编码后不能超过72字节");
        }
        return password;
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(fieldName + "不能为空");
        }
        return value.strip();
    }
}
