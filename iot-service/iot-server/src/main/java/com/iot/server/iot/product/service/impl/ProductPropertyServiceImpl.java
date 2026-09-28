package com.iot.server.iot.product.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.iot.product.dto.ProductPropertyCreateRequest;
import com.iot.server.iot.product.dto.ProductPropertyStatusRequest;
import com.iot.server.iot.product.dto.ProductPropertyUpdateRequest;
import com.iot.server.iot.product.entity.ProductPropertyEntity;
import com.iot.server.iot.product.mapper.ProductPropertyMapper;
import com.iot.server.iot.product.service.IProductPropertyService;
import com.iot.server.iot.product.service.IProductService;
import com.iot.server.iot.product.support.ThingModelSpecValidator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
@Service
public class ProductPropertyServiceImpl extends ServiceImpl<ProductPropertyMapper, ProductPropertyEntity>
                                        implements IProductPropertyService {

    private static final Set<String> ACCESS_MODES = Set.of("r", "rw");

    private static final Set<String> DATA_TYPES = Set.of(
            "int32",
            "float",
            "double",
            "text",
            "bool",
            "enum",
            "date"
    );

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");

    private final IProductService productService;
    private final ObjectMapper objectMapper;

    public ProductPropertyServiceImpl(IProductService productService,
                                      ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create (ProductPropertyCreateRequest request){

        validateCreateRequest(request);

        // detail 内部包含产品存在性和当前租户访问校验
        productService.detail(request.getProductId());

        ProductPropertyEntity property = new ProductPropertyEntity();
        property.setTenantId(TenantUtil.getTenantId());
        property.setProductId(request.getProductId());
        property.setIdentifier(request.getIdentifier().strip());
        property.setPropertyName(request.getPropertyName().strip());
        property.setAccessMode(request.getAccessMode().strip());
        property.setDataType(request.getDataType().strip());
        property.setSpecJson(toJson(request.getSpec()));
        property.setRequired(request.getRequired());
        property.setDescription(normalizeOptional(request.getDescription(), 500, "属性说明"));
        property.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        property.setStatus(1);

        try {
            if (!save(property)) {
                throw new ServiceException("创建物模型属性失败");
            }
            return property.getId();
        } catch (DuplicateKeyException exception) {
            throw new ServiceException("当前产品下属性标识符已存在");
        }
    }


    @Override
    public List<ProductPropertyEntity> listByProductId(Long productId, Integer status) {

        validateProductId(productId);

        // 防止查询不存在或不属于当前租户的产品
        productService.detail(productId);

        if (status != null && status != 0 && status != 1) {
            throw new ServiceException("属性状态只能是0或1");
        }

        return list(Wrappers.<ProductPropertyEntity>lambdaQuery()
                .eq(ProductPropertyEntity::getProductId, productId)
                .eq(status != null, ProductPropertyEntity::getStatus, status)
                .orderByAsc(ProductPropertyEntity::getSortOrder)
                .orderByAsc(ProductPropertyEntity::getCreateTime));
    }


    @Override
    public ProductPropertyEntity detail(Long id) {

        validatePropertyId(id);

        ProductPropertyEntity property = getById(id);

        if (property == null) {
            throw new ServiceException("物模型属性不存在或已删除");
        }

        return property;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductPropertyUpdateRequest request) {
        validateUpdateRequest(request);

        // 同时校验属性存在以及当前租户是否有权访问
        ProductPropertyEntity property = detail(request.getId());

        property.setPropertyName(request.getPropertyName().strip());
        property.setAccessMode(request.getAccessMode().strip());
        property.setDataType(request.getDataType().strip());
        property.setSpecJson(toJson(request.getSpec()));
        property.setRequired(request.getRequired());
        property.setDescription(normalizeOptional(
                request.getDescription(), 500, "属性说明"));
        property.setSortOrder(
                request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (!updateById(property)) {
            throw new ServiceException("修改物模型属性失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(ProductPropertyStatusRequest request) {
        if (request == null) {
            throw new ServiceException("物模型属性状态参数不能为空");
        }

        validatePropertyId(request.getId());

        if (request.getStatus() == null
                || (request.getStatus() != 0 && request.getStatus() != 1)) {
            throw new ServiceException("属性状态只能是0或1");
        }

        // detail 同时校验属性存在性和当前租户访问权限
        ProductPropertyEntity property = detail(request.getId());
        property.setStatus(request.getStatus());

        if (!updateById(property)) {
            throw new ServiceException("修改物模型属性状态失败");
        }
    }

    
    private void validateCreateRequest(ProductPropertyCreateRequest request) {

        if (request == null) {
            throw new ServiceException("物模型属性参数不能为空");
        }

        validateProductId(request.getProductId());

        String identifier = normalizeRequired(request.getIdentifier(), "属性标识符");

        if (identifier.length() > 64 || !IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new ServiceException("属性标识符格式不正确");
        }

        String propertyName = normalizeRequired(request.getPropertyName(), "属性名称");

        if (propertyName.length() > 100) {
            throw new ServiceException("属性名称不能超过100个字符");
        }

        String accessMode = normalizeRequired(request.getAccessMode(), "访问方式");

        if (!ACCESS_MODES.contains(accessMode)) {
            throw new ServiceException("访问方式只能是r或rw");
        }

        String dataType = normalizeRequired(request.getDataType(), "数据类型");

        if (!DATA_TYPES.contains(dataType)) {
            throw new ServiceException("数据类型不正确");
        }

        if (request.getSpec() == null || !request.getSpec().isObject()) {
            throw new ServiceException("数据规格必须是JSON对象");
        }

        validateSpec(dataType, request.getSpec());


        if (request.getRequired() == null
                || (request.getRequired() != 0
                && request.getRequired() != 1)) {
            throw new ServiceException("是否必需只能是0或1");
        }

        if (request.getSortOrder() != null
                && request.getSortOrder() < 0) {
            throw new ServiceException("排序号不能小于0");
        }

        normalizeOptional(request.getDescription(), 500, "属性说明");
    }

    private void validateUpdateRequest(ProductPropertyUpdateRequest request) {

        if (request == null) {
            throw new ServiceException("物模型属性参数不能为空");
        }

        validatePropertyId(request.getId());

        String propertyName = normalizeRequired(
                request.getPropertyName(), "属性名称");
        if (propertyName.length() > 100) {
            throw new ServiceException("属性名称不能超过100个字符");
        }

        String accessMode = normalizeRequired(
                request.getAccessMode(), "访问方式");
        if (!ACCESS_MODES.contains(accessMode)) {
            throw new ServiceException("访问方式只能是r或rw");
        }

        String dataType = normalizeRequired(
                request.getDataType(), "数据类型");
        if (!DATA_TYPES.contains(dataType)) {
            throw new ServiceException("数据类型不正确");
        }

        if (request.getSpec() == null || !request.getSpec().isObject()) {
            throw new ServiceException("数据规格必须是JSON对象");
        }
        validateSpec(dataType, request.getSpec());

        if (request.getRequired() == null
                || (request.getRequired() != 0
                && request.getRequired() != 1)) {
            throw new ServiceException("是否必需只能是0或1");
        }

        if (request.getSortOrder() != null
                && request.getSortOrder() < 0) {
            throw new ServiceException("排序号不能小于0");
        }

        normalizeOptional(request.getDescription(), 500, "属性说明");
    }


    private String toJson(JsonNode spec) {
        try {
            return objectMapper.writeValueAsString(spec);
        } catch (JsonProcessingException exception) {
            throw new ServiceException("数据规格JSON格式不正确");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ServiceException("产品ID不正确");
        }
    }

    private void validateSpec(String dataType, JsonNode spec) {
        ThingModelSpecValidator.validate(dataType, spec);
    }

    private void validatePropertyId(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException("物模型属性ID不正确");
        }
    }
    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(fieldName + "不能为空");
        }
        return value.strip();
    }

    private String normalizeOptional(String value,
                                     int maxLength,
                                     String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.strip();

        if (normalized.length() > maxLength) {
            throw new ServiceException(fieldName + "不能超过" + maxLength + "个字符");
        }

        return normalized;
    }
}
