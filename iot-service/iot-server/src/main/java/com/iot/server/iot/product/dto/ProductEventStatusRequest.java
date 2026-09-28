package com.iot.server.iot.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductEventStatusRequest {

    @NotNull(message = "物模型事件ID不能为空")
    @Positive(message = "物模型事件ID必须大于0")
    private Long id;

    @NotNull(message = "事件状态不能为空")
    @Min(value = 0, message = "事件状态只能是0或1")
    @Max(value = 1, message = "事件状态只能是0或1")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
