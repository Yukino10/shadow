package com.cislc.shadow.manage.core.bean.comm;

import com.cislc.shadow.manage.core.bean.shadow.ShadowDesiredDoc;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName DeviceState
 * @Description 通信设备状态
 * @Date 2019/7/1 19:42
 * @author szh
 **/
@Data
public class DeviceState {

    /** 设备上报数据 **/
    private ShadowDesiredDoc reported;
    /** 设备期望数据 **/
    private Map<String, Object> desired;

}
