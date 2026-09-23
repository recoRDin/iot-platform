package com.iot.server.system.tenant.controller;

import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.system.tenant.dto.TenantCreateRequest;
import com.iot.server.system.tenant.entity.TenantEntity;
import com.iot.server.system.tenant.service.ITenantService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("dev")
@RestController
@RequestMapping("/system/tenant")
@RequireRole(RoleConstants.ADMIN)
public class TenantController {

    private final ITenantService tenantService;

    public TenantController(ITenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * 创建租户。
     * 暂时仅供开发使用，后续接入平台管理员权限校验。
     */
    @ApiLog("创建租户")
    @PostMapping("/create")
    public R<String> create(
            @Valid @RequestBody TenantCreateRequest request) {

        Long id = tenantService.create(request);

        // 主键转为字符串，避免前端 JavaScript 大整数精度丢失。
        return R.data(id.toString());
    }

    /**
     * 根据数据库主键查询租户详情。
     */
    @ApiLog("查询租户详情")
    @GetMapping("/detail")
    public R<TenantEntity> detail(@RequestParam("id") Long id) {
        return R.data(tenantService.detail(id));
    }
}
