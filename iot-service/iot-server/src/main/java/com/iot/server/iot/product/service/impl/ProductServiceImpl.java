package com.iot.server.iot.product.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.TenantUtil;
import com.iot.server.iot.product.dto.ProductCreateRequest;
import com.iot.server.iot.product.dto.ProductStatusRequest;
import com.iot.server.iot.product.dto.ProductUpdateRequest;
import com.iot.server.iot.product.entity.ProductEntity;
import com.iot.server.iot.product.mapper.ProductMapper;
import com.iot.server.iot.product.service.IProductService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity>
        implements IProductService {

    private static final Set<String> DEVICE_TYPES = Set.of(
            "direct_connect", "gateway", "gateway_child");
    private static final Set<String> DATA_TYPES = Set.of("alink_json", "custom");
    private static final Pattern PRODUCT_KEY_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z0-9_-]{3,31}$");
    private static final Pattern PROTOCOL_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_-]{0,49}$");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductCreateRequest request) {
        validateCreateRequest(request);

        ProductEntity product = new ProductEntity();
        product.setTenantId(TenantUtil.getTenantId());
        product.setProductKey(request.getProductKey().strip());
        applyEditableFields(product,
                request.getProductName(),
                request.getProductDesc(),
                request.getDeviceType(),
                request.getLinkProtocol(),
                request.getConnectMode(),
                request.getDataType());
        product.setStatus(1);

        try {
            if (!save(product)) {
                throw new ServiceException("创建产品失败");
            }
            return product.getId();
        } catch (DuplicateKeyException exception) {
            throw new ServiceException("当前租户下产品编码已存在");
        }
    }

    @Override
    public ProductEntity detail(Long id) {

        validateId(id);
        ProductEntity product = getById(id);
        if (product == null) {
            throw new ServiceException("产品不存在或已删除");
        }
        return product;
    }

    @Override
    public IPage<ProductEntity> page(long current,
                                     long size,
                                     String productName,
                                     String productKey,
                                     Integer status) {
        if (current < 1) {
            throw new ServiceException("页码必须大于0");
        }
        if (size < 1 || size > 100) {
            throw new ServiceException("每页数量必须在1到100之间");
        }
        if (status != null && status != 0 && status != 1) {
            throw new ServiceException("产品状态只能是0或1");
        }

        String normalizedName = normalizeOptional(productName, 50, "产品名称");
        String normalizedKey = normalizeOptional(productKey, 32, "产品编码");

        return super.page(new Page<>(current, size),
                Wrappers.<ProductEntity>lambdaQuery()
                        .like(normalizedName != null,
                                ProductEntity::getProductName, normalizedName)
                        .eq(normalizedKey != null,
                                ProductEntity::getProductKey, normalizedKey)
                        .eq(status != null, ProductEntity::getStatus, status)
                        .orderByDesc(ProductEntity::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductUpdateRequest request) {
        if (request == null) {
            throw new ServiceException("产品参数不能为空");
        }
        ProductEntity product = detail(request.getId());
        applyEditableFields(product,
                request.getProductName(),
                request.getProductDesc(),
                request.getDeviceType(),
                request.getLinkProtocol(),
                request.getConnectMode(),
                request.getDataType());

        if (!updateById(product)) {
            throw new ServiceException("修改产品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(ProductStatusRequest request) {
        if (request == null || request.getStatus() == null
                || (request.getStatus() != 0 && request.getStatus() != 1)) {
            throw new ServiceException("产品状态只能是0或1");
        }
        ProductEntity product = detail(request.getId());
        product.setStatus(request.getStatus());
        if (!updateById(product)) {
            throw new ServiceException("修改产品状态失败");
        }
    }

    private void validateCreateRequest(ProductCreateRequest request) {
        if (request == null) {
            throw new ServiceException("产品参数不能为空");
        }
        String productKey = normalizeRequired(request.getProductKey(), "产品编码");
        if (!PRODUCT_KEY_PATTERN.matcher(productKey).matches()) {
            throw new ServiceException("产品编码格式不正确");
        }
    }

    private void applyEditableFields(ProductEntity product,
                                     String productName,
                                     String productDesc,
                                     String deviceType,
                                     String linkProtocol,
                                     String connectMode,
                                     String dataType) {
        String normalizedName = normalizeRequired(productName, "产品名称");
        if (normalizedName.length() > 50) {
            throw new ServiceException("产品名称不能超过50个字符");
        }

        String normalizedDescription = normalizeOptional(productDesc, 500, "产品描述");
        String normalizedDeviceType = normalizeRequired(deviceType, "设备类型");
        if (!DEVICE_TYPES.contains(normalizedDeviceType)) {
            throw new ServiceException("设备类型不正确");
        }

        String normalizedProtocol = normalizeRequired(linkProtocol, "连接协议");
        if (!PROTOCOL_PATTERN.matcher(normalizedProtocol).matches()) {
            throw new ServiceException("连接协议格式不正确");
        }

        String normalizedConnectMode = normalizeOptional(connectMode, 50, "联网方式");
        if (normalizedConnectMode != null
                && !PROTOCOL_PATTERN.matcher(normalizedConnectMode).matches()) {
            throw new ServiceException("联网方式格式不正确");
        }

        String normalizedDataType = normalizeRequired(dataType, "数据格式");
        if (!DATA_TYPES.contains(normalizedDataType)) {
            throw new ServiceException("数据格式不正确");
        }

        product.setProductName(normalizedName);
        product.setProductDesc(normalizedDescription);
        product.setDeviceType(normalizedDeviceType);
        product.setLinkProtocol(normalizedProtocol);
        product.setConnectMode(normalizedConnectMode);
        product.setDataType(normalizedDataType);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ServiceException("产品ID不正确");
        }
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ServiceException(fieldName + "不能为空");
        }
        return value.strip();
    }

    private String normalizeOptional(String value, int maxLength, String fieldName) {
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
