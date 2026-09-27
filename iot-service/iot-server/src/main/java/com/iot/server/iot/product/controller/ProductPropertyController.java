package com.iot.server.iot.product.controller;


import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.iot.product.dto.ProductPropertyCreateRequest;
import com.iot.server.iot.product.dto.ProductPropertyStatusRequest;
import com.iot.server.iot.product.dto.ProductPropertyUpdateRequest;
import com.iot.server.iot.product.entity.ProductPropertyEntity;
import com.iot.server.iot.product.service.IProductPropertyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/iot/product/property")
@RequireRole(RoleConstants.ADMIN)
public class ProductPropertyController {

    private final IProductPropertyService propertyService;

    public ProductPropertyController(IProductPropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @ApiLog("创建产品物模型属性")
    @PostMapping("/create")
    public R<String> create(@Valid @RequestBody ProductPropertyCreateRequest request){
        return  R.data(propertyService.create(request).toString());
    }

    @ApiLog("查询产品物模型属性列表")
    @GetMapping("/list")
    public R<List<ProductPropertyEntity>> list(
            @RequestParam("productId")
            @Positive(message = "产品ID必须大于0") Long productId,
            @RequestParam(value = "status", required = false)
            @Min(value = 0, message = "属性状态只能是0或1")
            @Max(value = 1, message = "属性状态只能是0或1")
            Integer status){

        return R.data(propertyService.listByProductId(productId, status));
    }


    @ApiLog("查询产品物模型属性详情")
    @GetMapping("/detail")
    public R<ProductPropertyEntity> detail(
            @RequestParam("id")
            @Positive(message = "属性ID必须大于0") Long id) {
        return R.data(propertyService.detail(id));
    }

    @ApiLog("修改产品物模型属性")
    @PostMapping("/update")
    public R<Void> update(
            @Valid @RequestBody ProductPropertyUpdateRequest request) {
        propertyService.update(request);
        return R.success();
    }

    @ApiLog("修改产品物模型属性状态")
    @PostMapping("/status")
    public R<Void> updateStatus(
            @Valid @RequestBody ProductPropertyStatusRequest request) {
        propertyService.updateStatus(request);
        return R.success();
    }
}
