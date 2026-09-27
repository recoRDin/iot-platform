package com.iot.server.iot.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.iot.core.mybatis.base.BaseEntity;

@TableName("iot_product")
public class ProductEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String productKey;
    private String productName;
    private String productDesc;
    private String deviceType;
    private String linkProtocol;
    private String connectMode;
    private String dataType;
    private Integer status;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
