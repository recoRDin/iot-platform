package com.iot.server.iot.product.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.iot.product.dto.ProductPropertyCreateRequest;
import com.iot.server.iot.product.dto.ProductPropertyStatusRequest;
import com.iot.server.iot.product.dto.ProductPropertyUpdateRequest;
import com.iot.server.iot.product.entity.ProductPropertyEntity;

import java.util.List;

public interface IProductPropertyService extends IService<ProductPropertyEntity> {

    //创建一个产品物模型，返回属性ID
    Long create(ProductPropertyCreateRequest request);

    //查询产品属性列表
    List<ProductPropertyEntity> listByProductId(Long productId, Integer status);

    //详情接口
    ProductPropertyEntity detail(Long id);

    //更新属性
    void update(ProductPropertyUpdateRequest request);

    //更新属性状态
    void updateStatus(ProductPropertyStatusRequest request);
}
