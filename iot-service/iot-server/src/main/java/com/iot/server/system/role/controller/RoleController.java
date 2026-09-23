package com.iot.server.system.role.controller;

import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.system.role.dto.RoleCreateRequest;
import com.iot.server.system.role.service.IRoleService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/system/role")
@RequireRole(RoleConstants.ADMIN)
public class RoleController {

    private final IRoleService roleService;

    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @ApiLog("创建角色")
    @PostMapping("/create")
    public R<String> create(@Valid @RequestBody RoleCreateRequest request) {
        return R.data(roleService.create(request).toString());
    }
}
