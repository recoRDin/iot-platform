package com.iot.server.iot.product.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.iot.product.dto.ProductServiceDefinitionCreateRequest;
import com.iot.server.iot.product.dto.ProductServiceDefinitionStatusRequest;
import com.iot.server.iot.product.dto.ProductServiceDefinitionUpdateRequest;
import com.iot.server.iot.product.entity.ProductServiceDefinitionEntity;

import java.util.List;

public interface IProductServiceDefinitionService
        extends IService<ProductServiceDefinitionEntity> {

    Long create(ProductServiceDefinitionCreateRequest request);

    List<ProductServiceDefinitionEntity> listByProductId(
            Long productId, Integer status);

    ProductServiceDefinitionEntity detail(Long id);

    void update(ProductServiceDefinitionUpdateRequest request);

    void updateStatus(ProductServiceDefinitionStatusRequest request);
}
