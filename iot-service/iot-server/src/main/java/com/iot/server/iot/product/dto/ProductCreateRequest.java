package com.iot.server.iot.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductCreateRequest {

    @NotBlank(message = "产品编码不能为空")
    @Size(min = 4, max = 32, message = "产品编码长度必须在4到32个字符之间")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_-]*$",
            message = "产品编码只能以字母开头，并包含字母、数字、下划线或横线")
    private String productKey;

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

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
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
