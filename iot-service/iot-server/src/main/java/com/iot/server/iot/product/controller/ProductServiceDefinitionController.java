package com.iot.server.iot.product.controller;

import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.iot.product.dto.ProductServiceDefinitionCreateRequest;
import com.iot.server.iot.product.dto.ProductServiceDefinitionStatusRequest;
import com.iot.server.iot.product.dto.ProductServiceDefinitionUpdateRequest;
import com.iot.server.iot.product.entity.ProductServiceDefinitionEntity;
import com.iot.server.iot.product.service.IProductServiceDefinitionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/iot/product/service")
@RequireRole(RoleConstants.ADMIN)
public class ProductServiceDefinitionController {

    private final IProductServiceDefinitionService serviceDefinitionService;

    public ProductServiceDefinitionController(
            IProductServiceDefinitionService serviceDefinitionService) {
        this.serviceDefinitionService = serviceDefinitionService;
    }

    @ApiLog("创建产品物模型服务")
    @PostMapping("/create")
    public R<String> create(
            @Valid @RequestBody ProductServiceDefinitionCreateRequest request) {
        return R.data(serviceDefinitionService.create(request).toString());
    }

    @ApiLog("查询产品物模型服务列表")
    @GetMapping("/list")
    public R<List<ProductServiceDefinitionEntity>> list(
            @RequestParam("productId")
            @Positive(message = "产品ID必须大于0") Long productId,
            @RequestParam(value = "status", required = false)
            @Min(value = 0, message = "服务状态只能是0或1")
            @Max(value = 1, message = "服务状态只能是0或1")
            Integer status) {
        return R.data(serviceDefinitionService.listByProductId(
                productId, status));
    }

    @ApiLog("查询产品物模型服务详情")
    @GetMapping("/detail")
    public R<ProductServiceDefinitionEntity> detail(
            @RequestParam("id")
            @Positive(message = "服务ID必须大于0") Long id) {
        return R.data(serviceDefinitionService.detail(id));
    }

    @ApiLog("修改产品物模型服务")
    @PostMapping("/update")
    public R<Void> update(
            @Valid @RequestBody ProductServiceDefinitionUpdateRequest request) {
        serviceDefinitionService.update(request);
        return R.success();
    }

    @ApiLog("修改产品物模型服务状态")
    @PostMapping("/status")
    public R<Void> updateStatus(
            @Valid @RequestBody ProductServiceDefinitionStatusRequest request) {
        serviceDefinitionService.updateStatus(request);
        return R.success();
    }
}
