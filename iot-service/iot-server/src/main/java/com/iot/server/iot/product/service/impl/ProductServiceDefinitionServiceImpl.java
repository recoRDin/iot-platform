package com.iot.server.iot.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.iot.product.dto.ProductServiceDefinitionCreateRequest;
import com.iot.server.iot.product.dto.ProductServiceDefinitionStatusRequest;
import com.iot.server.iot.product.dto.ProductServiceDefinitionUpdateRequest;
import com.iot.server.iot.product.entity.ProductServiceDefinitionEntity;
import com.iot.server.iot.product.mapper.ProductServiceDefinitionMapper;
import com.iot.server.iot.product.service.IProductService;
import com.iot.server.iot.product.service.IProductServiceDefinitionService;
import com.iot.server.iot.product.support.ThingModelParameterValidator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProductServiceDefinitionServiceImpl
        extends ServiceImpl<ProductServiceDefinitionMapper,
        ProductServiceDefinitionEntity>
        implements IProductServiceDefinitionService {

    private static final Set<String> CALL_TYPES = Set.of("sync", "async");

    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");

    private final ObjectMapper objectMapper;
    private final IProductService productService;

    public ProductServiceDefinitionServiceImpl(ObjectMapper objectMapper,
                                               IProductService productService) {
        this.objectMapper = objectMapper;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductServiceDefinitionCreateRequest request) {
        validateCreateRequest(request);
        productService.detail(request.getProductId());

        ProductServiceDefinitionEntity service =
                new ProductServiceDefinitionEntity();
        service.setTenantId(TenantUtil.getTenantId());
        service.setProductId(request.getProductId());
        service.setIdentifier(request.getIdentifier().strip());
        service.setServiceName(request.getServiceName().strip());
        service.setCallType(request.getCallType().strip());
        service.setInputJson(toJson(request.getInput(), "服务输入参数"));
        service.setOutputJson(toJson(request.getOutput(), "服务输出参数"));
        service.setRequired(request.getRequired());
        service.setDescription(normalizeOptional(
                request.getDescription(), 500, "服务说明"));
        service.setSortOrder(
                request.getSortOrder() == null ? 0 : request.getSortOrder());
        service.setStatus(1);

        try {
            if (!save(service)) {
                throw new ServiceException("创建物模型服务失败");
            }
            return service.getId();
        } catch (DuplicateKeyException exception) {
            throw new ServiceException("当前产品下服务标识符已存在");
        }
    }

    @Override
    public List<ProductServiceDefinitionEntity> listByProductId(
            Long productId, Integer status) {
        validateProductId(productId);
        productService.detail(productId);
        validateStatus(status);

        return list(Wrappers.<ProductServiceDefinitionEntity>lambdaQuery()
                .eq(ProductServiceDefinitionEntity::getProductId, productId)
                .eq(status != null,
                        ProductServiceDefinitionEntity::getStatus, status)
                .orderByAsc(ProductServiceDefinitionEntity::getSortOrder)
                .orderByAsc(ProductServiceDefinitionEntity::getCreateTime));
    }

    @Override
    public ProductServiceDefinitionEntity detail(Long id) {
        validateServiceId(id);

        ProductServiceDefinitionEntity service = getById(id);
        if (service == null) {
            throw new ServiceException("物模型服务不存在或已删除");
        }
        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductServiceDefinitionUpdateRequest request) {
        validateUpdateRequest(request);

        ProductServiceDefinitionEntity service = detail(request.getId());
        service.setServiceName(request.getServiceName().strip());
        service.setCallType(request.getCallType().strip());
        service.setInputJson(toJson(request.getInput(), "服务输入参数"));
        service.setOutputJson(toJson(request.getOutput(), "服务输出参数"));
        service.setRequired(request.getRequired());
        service.setDescription(normalizeOptional(
                request.getDescription(), 500, "服务说明"));
        service.setSortOrder(
                request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (!updateById(service)) {
            throw new ServiceException("修改物模型服务失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(ProductServiceDefinitionStatusRequest request) {
        if (request == null) {
            throw new ServiceException("物模型服务状态参数不能为空");
        }
        validateServiceId(request.getId());
        if (request.getStatus() == null) {
            throw new ServiceException("服务状态不能为空");
        }
        validateStatus(request.getStatus());

        ProductServiceDefinitionEntity service = detail(request.getId());
        service.setStatus(request.getStatus());
        if (!updateById(service)) {
            throw new ServiceException("修改物模型服务状态失败");
        }
    }

    private void validateCreateRequest(
            ProductServiceDefinitionCreateRequest request) {
        if (request == null) {
            throw new ServiceException("物模型服务参数不能为空");
        }
        validateProductId(request.getProductId());
        validateCommon(
                request.getIdentifier(), request.getServiceName(),
                request.getCallType(), request.getInput(), request.getOutput(),
                request.getRequired(), request.getDescription(),
                request.getSortOrder(), true);
    }

    private void validateUpdateRequest(
            ProductServiceDefinitionUpdateRequest request) {
        if (request == null) {
            throw new ServiceException("物模型服务参数不能为空");
        }
        validateServiceId(request.getId());
        validateCommon(
                null, request.getServiceName(), request.getCallType(),
                request.getInput(), request.getOutput(), request.getRequired(),
                request.getDescription(), request.getSortOrder(), false);
    }

    private void validateCommon(String identifier,
                                String serviceName,
                                String callType,
                                JsonNode input,
                                JsonNode output,
                                Integer required,
                                String description,
                                Integer sortOrder,
                                boolean validateIdentifier) {
        if (validateIdentifier) {
            String normalizedIdentifier = normalizeRequired(
                    identifier, "服务标识符");
            if (normalizedIdentifier.length() > 64
                    || !IDENTIFIER_PATTERN.matcher(normalizedIdentifier).matches()) {
                throw new ServiceException("服务标识符格式不正确");
            }
        }

        String normalizedName = normalizeRequired(serviceName, "服务名称");
        if (normalizedName.length() > 100) {
            throw new ServiceException("服务名称不能超过100个字符");
        }

        String normalizedCallType = normalizeRequired(callType, "调用方式");
        if (!CALL_TYPES.contains(normalizedCallType)) {
            throw new ServiceException("调用方式只能是sync或async");
        }

        ThingModelParameterValidator.validateArray(input, "服务输入参数");
        ThingModelParameterValidator.validateArray(output, "服务输出参数");

        if (required == null || (required != 0 && required != 1)) {
            throw new ServiceException("是否必需只能是0或1");
        }
        if (sortOrder != null && sortOrder < 0) {
            throw new ServiceException("排序号不能小于0");
        }
        normalizeOptional(description, 500, "服务说明");
    }

    private String toJson(JsonNode value, String displayName) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new ServiceException(displayName + "JSON格式不正确");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ServiceException("产品ID不正确");
        }
    }

    private void validateServiceId(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException("物模型服务ID不正确");
        }
    }

    private void validateStatus(Integer status) {
        if (status != null && status != 0 && status != 1) {
            throw new ServiceException("服务状态只能是0或1");
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
            throw new ServiceException(
                    fieldName + "不能超过" + maxLength + "个字符");
        }
        return normalized;
    }
}
