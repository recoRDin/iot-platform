package com.iot.server.iot.product.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.iot.product.dto.ProductEventCreateRequest;
import com.iot.server.iot.product.dto.ProductEventStatusRequest;
import com.iot.server.iot.product.dto.ProductEventUpdateRequest;
import com.iot.server.iot.product.entity.ProductEventEntity;
import com.iot.server.iot.product.mapper.ProductEventMapper;
import com.iot.server.iot.product.service.IProductEventService;
import com.iot.server.iot.product.service.IProductService;
import com.iot.server.iot.product.support.ThingModelParameterValidator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProductEventServiceImpl
        extends ServiceImpl<ProductEventMapper, ProductEventEntity>
        implements IProductEventService {

    private static final Set<String> EVENT_TYPES =
            Set.of("info", "alert", "error");

    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_]*$");

    private final ObjectMapper objectMapper;
    private final IProductService productService;

    public ProductEventServiceImpl(ObjectMapper objectMapper,
                                   IProductService productService) {
        this.objectMapper = objectMapper;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductEventCreateRequest request) {
        validateCreateRequest(request);

        // detail 内部同时校验产品存在性和当前租户访问权限
        productService.detail(request.getProductId());

        ProductEventEntity event = new ProductEventEntity();
        event.setTenantId(TenantUtil.getTenantId());
        event.setProductId(request.getProductId());
        event.setIdentifier(request.getIdentifier().strip());
        event.setEventName(request.getEventName().strip());
        event.setEventType(request.getEventType().strip());
        event.setOutputJson(toJson(request.getOutput()));
        event.setRequired(request.getRequired());
        event.setDescription(normalizeOptional(request.getDescription(), 500, "事件说明"));
        event.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        event.setStatus(1);

        try {
            if (!save(event)) {
                throw new ServiceException("创建物模型事件失败");
            }
            return event.getId();
        } catch (DuplicateKeyException exception) {
            throw new ServiceException("当前产品下事件标识符已存在");
        }
    }

    @Override
    public List<ProductEventEntity> listByProductId(
            Long productId, Integer status) {
        validateProductId(productId);

        // 防止查询不存在或不属于当前租户的产品
        productService.detail(productId);

        if (status != null && status != 0 && status != 1) {
            throw new ServiceException("事件状态只能是0或1");
        }

        return list(Wrappers.<ProductEventEntity>lambdaQuery()
                .eq(ProductEventEntity::getProductId, productId)
                .eq(status != null, ProductEventEntity::getStatus, status)
                .orderByAsc(ProductEventEntity::getSortOrder)
                .orderByAsc(ProductEventEntity::getCreateTime));
    }

    @Override
    public ProductEventEntity detail(Long id) {
        validateEventId(id);

        ProductEventEntity event = getById(id);
        if (event == null) {
            throw new ServiceException("物模型事件不存在或已删除");
        }
        return event;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductEventUpdateRequest request) {
        validateUpdateRequest(request);

        ProductEventEntity event = detail(request.getId());
        event.setEventName(request.getEventName().strip());
        event.setEventType(request.getEventType().strip());
        event.setOutputJson(toJson(request.getOutput()));
        event.setRequired(request.getRequired());
        event.setDescription(normalizeOptional(
                request.getDescription(), 500, "事件说明"));
        event.setSortOrder(
                request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (!updateById(event)) {
            throw new ServiceException("修改物模型事件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(ProductEventStatusRequest request) {
        if (request == null) {
            throw new ServiceException("物模型事件状态参数不能为空");
        }

        validateEventId(request.getId());
        if (request.getStatus() == null
                || (request.getStatus() != 0 && request.getStatus() != 1)) {
            throw new ServiceException("事件状态只能是0或1");
        }

        ProductEventEntity event = detail(request.getId());
        event.setStatus(request.getStatus());
        if (!updateById(event)) {
            throw new ServiceException("修改物模型事件状态失败");
        }
    }

    private void validateCreateRequest(ProductEventCreateRequest request) {
        if (request == null) {
            throw new ServiceException("物模型事件参数不能为空");
        }

        validateProductId(request.getProductId());

        String identifier = normalizeRequired(
                request.getIdentifier(), "事件标识符");
        if (identifier.length() > 64
                || !IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new ServiceException("事件标识符格式不正确");
        }

        String eventName = normalizeRequired(request.getEventName(), "事件名称");
        if (eventName.length() > 100) {
            throw new ServiceException("事件名称不能超过100个字符");
        }

        String eventType = normalizeRequired(request.getEventType(), "事件类型");
        if (!EVENT_TYPES.contains(eventType)) {
            throw new ServiceException("事件类型只能是info、alert或error");
        }

        validateOutput(request.getOutput());

        if (request.getRequired() == null
                || (request.getRequired() != 0 && request.getRequired() != 1)) {
            throw new ServiceException("是否必需只能是0或1");
        }
        if (request.getSortOrder() != null && request.getSortOrder() < 0) {
            throw new ServiceException("排序号不能小于0");
        }
        normalizeOptional(request.getDescription(), 500, "事件说明");
    }

    private void validateUpdateRequest(ProductEventUpdateRequest request) {
        if (request == null) {
            throw new ServiceException("物模型事件参数不能为空");
        }

        validateEventId(request.getId());

        String eventName = normalizeRequired(request.getEventName(), "事件名称");
        if (eventName.length() > 100) {
            throw new ServiceException("事件名称不能超过100个字符");
        }

        String eventType = normalizeRequired(request.getEventType(), "事件类型");
        if (!EVENT_TYPES.contains(eventType)) {
            throw new ServiceException("事件类型只能是info、alert或error");
        }

        validateOutput(request.getOutput());

        if (request.getRequired() == null
                || (request.getRequired() != 0 && request.getRequired() != 1)) {
            throw new ServiceException("是否必需只能是0或1");
        }
        if (request.getSortOrder() != null && request.getSortOrder() < 0) {
            throw new ServiceException("排序号不能小于0");
        }
        normalizeOptional(request.getDescription(), 500, "事件说明");
    }

    private void validateOutput(JsonNode output) {
        ThingModelParameterValidator.validateArray(output, "事件输出参数");
    }

    private String toJson(JsonNode output) {
        try {
            return objectMapper.writeValueAsString(output);
        } catch (JsonProcessingException exception) {
            throw new ServiceException("事件输出参数JSON格式不正确");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ServiceException("产品ID不正确");
        }
    }

    private void validateEventId(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException("物模型事件ID不正确");
        }
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(fieldName + "不能为空");
        }
        return value.strip();
    }

    private String normalizeOptional(
            String value, int maxLength, String fieldName) {
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
