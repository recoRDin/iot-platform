package com.iot.server.iot.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.iot.product.dto.ProductCreateRequest;
import com.iot.server.iot.product.dto.ProductStatusRequest;
import com.iot.server.iot.product.dto.ProductUpdateRequest;
import com.iot.server.iot.product.entity.ProductEntity;

public interface IProductService extends IService<ProductEntity> {

    Long create(ProductCreateRequest request);

    ProductEntity detail(Long id);

    IPage<ProductEntity> page(long current,
                              long size,
                              String productName,
                              String productKey,
                              Integer status);

    void update(ProductUpdateRequest request);

    void updateStatus(ProductStatusRequest request);
}
