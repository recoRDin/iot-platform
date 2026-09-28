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

public class ProductServiceDefinitionCreateRequest {

    @NotNull(message = "产品ID不能为空")
    @Positive(message = "产品ID必须大于0")
    private Long productId;

    @NotBlank(message = "服务标识符不能为空")
    @Size(max = 64, message = "服务标识符不能超过64个字符")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "服务标识符只能以字母开头，并包含字母、数字或下划线")
    private String identifier;

    @NotBlank(message = "服务名称不能为空")
    @Size(max = 100, message = "服务名称不能超过100个字符")
    private String serviceName;

    @NotBlank(message = "调用方式不能为空")
    @Pattern(regexp = "^(sync|async)$",
            message = "调用方式只能是sync或async")
    private String callType;

    @NotNull(message = "服务输入参数不能为空")
    private JsonNode input;

    @NotNull(message = "服务输出参数不能为空")
    private JsonNode output;

    @NotNull(message = "是否必需不能为空")
    @Min(value = 0, message = "是否必需只能是0或1")
    @Max(value = 1, message = "是否必需只能是0或1")
    private Integer required;

    @Size(max = 500, message = "服务说明不能超过500个字符")
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public JsonNode getInput() {
        return input;
    }

    public void setInput(JsonNode input) {
        this.input = input;
    }

    public JsonNode getOutput() {
        return output;
    }

    public void setOutput(JsonNode output) {
        this.output = output;
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
