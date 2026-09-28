package com.iot.server.iot.product.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.iot.server.iot.product.dto.ProductEventCreateRequest;
import com.iot.server.iot.product.dto.ProductEventStatusRequest;
import com.iot.server.iot.product.dto.ProductEventUpdateRequest;
import com.iot.server.iot.product.entity.ProductEventEntity;

import java.util.List;

public interface IProductEventService extends IService<ProductEventEntity> {

    //创建事件定义并返回事件id
    Long create(ProductEventCreateRequest request);


    //查询某个产品的事件，status为空时查询全部，其他按0 1状态过滤
    List<ProductEventEntity> listByProductId(Long productId, Integer status);

    ProductEventEntity detail(Long id);

    void update(ProductEventUpdateRequest request);

    void updateStatus(ProductEventStatusRequest request);
}
