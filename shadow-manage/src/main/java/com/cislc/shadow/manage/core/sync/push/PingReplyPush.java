package com.cislc.shadow.manage.core.sync.push;

import com.cislc.shadow.manage.core.bean.comm.RePayload;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * 心跳回复推送
 *
 * @author szh
 * @since 2020/12/25 12:23
 **/
public class PingReplyPush extends MessagePush {

    /**
     * @Description 更新成功后的推送
     * @param deviceId 设备id
     * @param commParams 通信参数
     * @param version 影子版本
     * @author szh
     * @Date 2019/5/3 17:52
     */
    public void push(String deviceId, CommParams commParams, int version) {
        RePayload rePayload = new RePayload(ShadowConst.PAYLOAD_STATUS_SUCCESS, version);
        assembleAndPublish(deviceId, ShadowConst.REPLY_METHOD_PONG, rePayload,
                commParams, System.currentTimeMillis(), version);
    }

}
