package com.cislc.shadow.manage.core.bean.comm;

import lombok.Data;

/**
 * ShadowOpsBean
 * 影子通信操作bean
 * @since 2019/4/28 20:50
 * @author szh
 **/
@Data
public class ShadowOpsBean {

    /** 设备id **/
    private String deviceId;
    /** 操作类型 **/
    private String method;
    /** 设备状态 **/
    private DeviceState state;
    /** 设备版本 **/
    private int version;

}
