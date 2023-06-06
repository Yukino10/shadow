package com.cislc.shadow.manage.core.sync.keepAlive;

import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;

import java.util.List;

/**
 * 设备丢失连接策略
 *
 * @author szh
 * @since 2020/12/25 14:59
 **/
public interface DeviceLostStrategy {

    /**
     * 处理数据连接的设备
     *
     * @param lostDevices 失去连接的设备列表
     * @author szh
     * @since 2020/12/25 15:03
     */
    void process(List<ShadowEntity> lostDevices);

}
