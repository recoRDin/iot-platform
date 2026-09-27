package com.iot.server.iot.product.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductPropertyCreateRequest {

    @NotNull(message = "产品ID不能为空")
    @Positive(message = "产品ID必须大于0")
    private Long productId;

    @NotBlank(message = "属性标识符不能为空")
    @Size(max = 64, message = "属性标识符不能超过64个字符")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "属性标识符只能以字母开头，并包含字母、数字或下划线")
    private String identifier;

    @NotBlank(message = "属性名称不能为空")
    @Size(max = 100, message = "属性名称不能超过100个字符")
    private String propertyName;

    @NotBlank(message = "访问方式不能为空")
    @Pattern(regexp = "^(r|rw)$", message = "访问方式只能是r或rw")
    private String accessMode;

    @NotBlank(message = "数据类型不能为空")
    @Pattern(regexp = "^(int32|float|double|text|bool|enum|date)$",
            message = "数据类型不正确")
    private String dataType;

    @NotNull(message = "数据规格不能为空")
    private JsonNode spec;

    @NotNull(message = "是否必需不能为空")
    @Min(value = 0, message = "是否必需只能是0或1")
    @Max(value = 1, message = "是否必需只能是0或1")
    private Integer required;

    @Size(max = 500, message = "属性说明不能超过500个字符")
    private String description;

    @PositiveOrZero(message = "排序号不能小于0")
    private Integer sortOrder;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public JsonNode getSpec() {
        return spec;
    }

    public void setSpec(JsonNode spec) {
        this.spec = spec;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
