package com.communication.TestFiles;

import com.communication.common.DeviceAddr;

/**
 * @Author: bin
 * @Date: 2019/11/7 17:29
 * @Description:
 * 模拟一个影子设备，其中包含了设备的id 和设备的通信地址信息
 */
public class ShadowModel {
    String id;
    DeviceAddr deviceAddr;

    public ShadowModel(String id, DeviceAddr deviceAddr) {
        this.id = id;
        this.deviceAddr = deviceAddr;
    }
}
