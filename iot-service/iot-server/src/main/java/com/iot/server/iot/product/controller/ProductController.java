package com.iot.server.iot.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iot.core.log.annotation.ApiLog;
import com.iot.core.tool.api.R;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.constant.RoleConstants;
import com.iot.server.iot.product.dto.ProductCreateRequest;
import com.iot.server.iot.product.dto.ProductStatusRequest;
import com.iot.server.iot.product.dto.ProductUpdateRequest;
import com.iot.server.iot.product.entity.ProductEntity;
import com.iot.server.iot.product.service.IProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/iot/product")
@RequireRole(RoleConstants.ADMIN)
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @ApiLog("创建产品")
    @PostMapping("/create")
    public R<String> create(@Valid @RequestBody ProductCreateRequest request) {
        return R.data(productService.create(request).toString());
    }

    @ApiLog("查询产品详情")
    @GetMapping("/detail")
    public R<ProductEntity> detail(
            @RequestParam("id") @Positive(message = "产品ID必须大于0") Long id) {
        return R.data(productService.detail(id));
    }

    @ApiLog("分页查询产品")
    @GetMapping("/page")
    public R<IPage<ProductEntity>> page(
            @RequestParam(value = "current", defaultValue = "1")
            @Min(value = 1, message = "页码必须大于0") long current,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于0")
            @Max(value = 100, message = "每页数量不能超过100") long size,
            @RequestParam(value = "productName", required = false)
            @Size(max = 50, message = "产品名称不能超过50个字符") String productName,
            @RequestParam(value = "productKey", required = false)
            @Size(max = 32, message = "产品编码不能超过32个字符") String productKey,
            @RequestParam(value = "status", required = false)
            @Min(value = 0, message = "产品状态只能是0或1")
            @Max(value = 1, message = "产品状态只能是0或1") Integer status) {
        return R.data(productService.page(
                current, size, productName, productKey, status));
    }

    @ApiLog("修改产品")
    @PostMapping("/update")
    public R<Void> update(@Valid @RequestBody ProductUpdateRequest request) {
        productService.update(request);
        return R.success();
    }

    @ApiLog("修改产品状态")
    @PostMapping("/status")
    public R<Void> updateStatus(@Valid @RequestBody ProductStatusRequest request) {
        productService.updateStatus(request);
        return R.success();
    }
}
