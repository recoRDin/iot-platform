package com.iot.server.iot.product.controller;

import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.iot.product.dto.ProductEventCreateRequest;
import com.iot.server.iot.product.dto.ProductEventStatusRequest;
import com.iot.server.iot.product.dto.ProductEventUpdateRequest;
import com.iot.server.iot.product.entity.ProductEventEntity;
import com.iot.server.iot.product.service.IProductEventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/iot/product/event")
@RequireRole(RoleConstants.ADMIN)
public class ProductEventController {

    private final IProductEventService productEventService;

    public ProductEventController(IProductEventService productEventService) {
        this.productEventService = productEventService;
    }

    @ApiLog("创建产品物模型事件")
    @PostMapping("/create")
    public R<String> create(@Valid @RequestBody ProductEventCreateRequest request) {
        return R.data(productEventService.create(request).toString());
    }

    @ApiLog("查询产品物模型事件列表")
    @GetMapping("/list")
    public R<List<ProductEventEntity>> list(
            @RequestParam("productId")
            @Positive(message = "产品ID必须大于0") Long productId,

            @RequestParam(value = "status", required = false)
            @Min(value = 0, message = "事件状态只能是0或1")
            @Max(value = 1, message = "事件状态只能是0或1")
            Integer status){

        return R.data(productEventService.listByProductId(productId, status));
    }

    @ApiLog("查询产品物模型事件详情")
    @GetMapping("/detail")
    public R<ProductEventEntity> detail(
            @RequestParam("id")
            @Positive(message = "事件ID必须大于0") Long id) {
        return R.data(productEventService.detail(id));
    }

    @ApiLog("修改产品物模型事件")
    @PostMapping("/update")
    public R<Void> update(
            @Valid @RequestBody ProductEventUpdateRequest request) {
        productEventService.update(request);
        return R.success();
    }

    @ApiLog("修改产品物模型事件状态")
    @PostMapping("/status")
    public R<Void> updateStatus(
            @Valid @RequestBody ProductEventStatusRequest request) {
        productEventService.updateStatus(request);
        return R.success();
    }
}
