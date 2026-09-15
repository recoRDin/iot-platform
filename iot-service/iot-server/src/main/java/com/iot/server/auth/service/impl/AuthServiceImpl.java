package com.iot.server.auth.service.impl;

import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.auth.dto.LoginRequest;
import com.iot.server.auth.model.AuthenticatedUser;
import com.iot.server.auth.service.IAuthService;
import com.iot.server.system.role.service.IRoleService;
import com.iot.server.system.tenant.entity.TenantEntity;
import com.iot.server.system.tenant.service.ITenantService;
import com.iot.server.system.user.entity.UserEntity;
import com.iot.server.system.user.service.IUserRoleService;
import com.iot.server.system.user.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final String LOGIN_FAILED_MESSAGE = "租户、账号或密码错误";

    private final ITenantService tenantService;
    private final IUserService userService;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(ITenantService tenantService,
                           IUserService userService,
                           IUserRoleService userRoleService,
                           IRoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUser authenticate(LoginRequest request) {

        if (request == null) {
            throw new ServiceException(LOGIN_FAILED_MESSAGE);
        }

        //获取用户信息
        String tenantId = normalize(request.getTenantId());
        String account = normalize(request.getAccount());
        String password = request.getPassword();

        //根据租户编号查询
        TenantEntity tenant = tenantService.findByTenantId(tenantId);

        if (tenant == null || !Integer.valueOf(1).equals(tenant.getStatus())) {
            throw new ServiceException(LOGIN_FAILED_MESSAGE);
        }

        return TenantUtil.use(tenantId, () -> authenticateInTenant(tenantId, account, password));
    }

    private AuthenticatedUser authenticateInTenant(String tenantId, String account, String password) {

        //根据账户查询
        UserEntity user = userService.findByAccount(account);

        if (user == null || !Integer.valueOf(1).equals(user.getStatus())
                || password == null
                || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException(LOGIN_FAILED_MESSAGE);
        }

        List<Long> roleIds = userRoleService.listRoleIds(user.getId());
        List<String> roleCodes = roleService.listEnabledRoleCodes(roleIds);

        if (roleCodes.isEmpty()) {
            throw new ServiceException("用户未分配有效角色");
        }

        return new AuthenticatedUser(
                user.getId().toString(),
                tenantId,
                user.getAccount(),
                user.getRealName(),
                roleCodes);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(LOGIN_FAILED_MESSAGE);
        }
        return value.strip();
    }
}
