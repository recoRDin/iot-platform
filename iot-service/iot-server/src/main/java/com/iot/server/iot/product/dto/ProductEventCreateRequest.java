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

public class ProductEventCreateRequest {

    @NotNull(message = "产品ID不能为空")
    @Positive(message = "产品ID必须大于0")
    private Long productId;

    @NotBlank(message = "事件标识符不能为空")
    @Size(max = 64, message = "事件标识符不能超过64个字符")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "事件标识符只能以字母开头，并包含字母、数字或下划线")
    private String identifier;

    @NotBlank(message = "事件名称不能为空")
    @Size(max = 100, message = "事件名称不能超过100个字符")
    private String eventName;

    @NotBlank(message = "事件类型不能为空")
    @Pattern(regexp = "^(info|alert|error)$",
            message = "事件类型只能是info、alert或error")
    private String eventType;

    @NotNull(message = "事件输出参数不能为空")
    private JsonNode output;

    @NotNull(message = "是否必需不能为空")
    @Min(value = 0, message = "是否必需只能是0或1")
    @Max(value = 1, message = "是否必需只能是0或1")
    private Integer required;

    @Size(max = 500, message = "事件说明不能超过500个字符")
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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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
