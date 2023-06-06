package com.shadow.web.model.product;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private Integer id;

    private String name;

    private String fileUrl;

    private Integer deviceId;

    private String encryption;

    private String operateSystem;

    private Integer protocolSize;

    private Integer serverSize;

    private String description;

    private Integer updated;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;

    private static final long serialVersionUID = 1L;

    public Product() {}

    public Product(String name, Integer deviceId, int protocolSize, int serverSize, String description) {
        this.name = name;
        this.deviceId = deviceId;
        this.protocolSize = protocolSize;
        this.serverSize = serverSize;
        this.description = description;
    }

    public Product(String name, Integer deviceId,String encryption,String operateSystem, int protocolSize, int serverSize, String description) {
        this.name = name;
        this.deviceId = deviceId;
        this.encryption = encryption;
        this.operateSystem = operateSystem;
        this.protocolSize = protocolSize;
        this.serverSize = serverSize;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl == null ? null : fileUrl.trim();
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption == null ? null : encryption.trim();
    }

    public String getOperateSystem() {
        return operateSystem;
    }

    public void setOperateSystem(String operateSystem) {
        this.operateSystem = operateSystem == null ? null : operateSystem.trim();
    }

    public Integer getProtocolSize() {
        return protocolSize;
    }

    public void setProtocolSize(Integer protocolSize) {
        this.protocolSize = protocolSize;
    }

    public Integer getServerSize() {
        return serverSize;
    }

    public void setServerSize(Integer serverSize) {
        this.serverSize = serverSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getUpdated() {
        return updated;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}