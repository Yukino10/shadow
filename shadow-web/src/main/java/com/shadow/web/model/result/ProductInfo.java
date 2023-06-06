package com.shadow.web.model.result;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/**
 * @author wangzhendong
 * @since 2019/10/31 14:02
 */
@Data
public class ProductInfo {

    private Integer id;

    private String name;

    private String fileUrl;

    private Integer deviceId;

    private String deviceName;

    private String encryption;

    private String operateSystem;

    private Integer protocolSize;

    private Integer serverSize;

    private String description;

    @Getter(value = AccessLevel.NONE)
    private Integer updated;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;

    private List<String> protocolList;

    private List<String> serverList;

    public ProductInfo(){}

    public boolean isUpdated() {
        return updated == 1;
    }

}
