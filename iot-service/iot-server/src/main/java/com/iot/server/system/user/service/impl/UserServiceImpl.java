package com.iot.server.system.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.system.user.dto.UserCreateRequest;
import com.iot.server.system.user.entity.UserEntity;
import com.iot.server.system.user.mapper.UserMapper;
import com.iot.server.system.user.service.IUserRoleService;
import com.iot.server.system.user.service.IUserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
        implements IUserService {

    private static final Pattern ACCOUNT_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_-]*$");

    private final PasswordEncoder passwordEncoder;
    private final IUserRoleService userRoleService;

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           IUserRoleService userRoleService) {
        this.passwordEncoder = passwordEncoder;
        this.userRoleService = userRoleService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserCreateRequest request) {
        if (request == null) {
            throw new ServiceException("用户参数不能为空");
        }

        String account = normalize(request.getAccount(), "登录账号");
        String realName = normalize(request.getRealName(), "用户姓名");
        String password = request.getPassword();

        if (account.length() < 4 || account.length() > 50
                || !ACCOUNT_PATTERN.matcher(account).matches()) {
            throw new ServiceException("登录账号格式不正确");
        }
        if (realName.length() > 50) {
            throw new ServiceException("用户姓名不能超过50个字符");
        }
        if (password == null || password.length() < 8
                || password.length() > 64 || password.isBlank()) {
            throw new ServiceException("登录密码长度必须在8到64个字符之间");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ServiceException("登录密码编码后不能超过72字节");
        }
        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            throw new ServiceException("用户角色不能为空");
        }

        UserEntity user = new UserEntity();
        user.setTenantId(TenantUtil.getTenantId());
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setStatus(1);

        try {
            if (!save(user)) {
                throw new ServiceException("创建用户失败");
            }
            userRoleService.assignRoles(user.getId(), request.getRoleIds());
            return user.getId();
        } catch (DuplicateKeyException exception) {
            throw new ServiceException("当前租户下登录账号已存在");
        }
    }

    @Override
    public UserEntity findByAccount(String account) {
        if (account == null || account.isBlank()) {
            return null;
        }
        return getOne(Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getAccount, account.strip()));
    }

    private String normalize(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(fieldName + "不能为空");
        }
        return value.strip();
    }
}
