package com.shadow.web.model.product;

import java.io.Serializable;
import java.util.Date;

public class ProductServer implements Serializable {
    private Integer id;

    private Integer productId;

    private String server;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public ProductServer(){}

    public ProductServer(Integer productId, String server) {
        this.productId = productId;
        this.server = server;
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server == null ? null : server.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}