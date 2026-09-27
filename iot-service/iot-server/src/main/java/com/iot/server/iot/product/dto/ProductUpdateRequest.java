package com.iot.server.iot.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductUpdateRequest {

    @NotNull(message = "产品ID不能为空")
    @Positive(message = "产品ID必须大于0")
    private Long id;

    @NotBlank(message = "产品名称不能为空")
    @Size(max = 50, message = "产品名称不能超过50个字符")
    private String productName;

    @Size(max = 500, message = "产品描述不能超过500个字符")
    private String productDesc;

    @NotBlank(message = "设备类型不能为空")
    @Pattern(regexp = "^(direct_connect|gateway|gateway_child)$",
            message = "设备类型不正确")
    private String deviceType;

    @NotBlank(message = "连接协议不能为空")
    @Size(max = 50, message = "连接协议不能超过50个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_-]*$", message = "连接协议格式不正确")
    private String linkProtocol;

    @Size(max = 50, message = "联网方式不能超过50个字符")
    @Pattern(regexp = "^$|^[a-z][a-z0-9_-]*$", message = "联网方式格式不正确")
    private String connectMode;

    @NotBlank(message = "数据格式不能为空")
    @Pattern(regexp = "^(alink_json|custom)$", message = "数据格式不正确")
    private String dataType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLinkProtocol() {
        return linkProtocol;
    }

    public void setLinkProtocol(String linkProtocol) {
        this.linkProtocol = linkProtocol;
    }

    public String getConnectMode() {
        return connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
