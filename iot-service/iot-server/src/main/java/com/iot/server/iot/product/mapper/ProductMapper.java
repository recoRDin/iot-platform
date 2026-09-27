package com.iot.server.iot.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.server.iot.product.entity.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
}
