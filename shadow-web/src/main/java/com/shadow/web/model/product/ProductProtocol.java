package com.shadow.web.model.product;

import java.io.Serializable;
import java.util.Date;

public class ProductProtocol implements Serializable {
    private Integer id;

    private Integer productId;

    private String protocol;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public ProductProtocol(){}

    public ProductProtocol(Integer productId, String protocol) {
        this.productId = productId;
        this.protocol = protocol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol == null ? null : protocol.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}