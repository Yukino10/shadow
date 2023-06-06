package com.cislc.shadow.manage.core.sync.push;

import com.cislc.shadow.manage.core.bean.comm.RePayload;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.bean.shadow.ShadowDoc;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * @ClassName ControlPush
 * @Description 服务器下发属性推送
 * @Date 2019/5/3 20:49
 * @author szh
 **/
public class ControlPush extends MessagePush {

    /**
     * @Description 推送属性
     * @param deviceId 设备id
     * @param commParams 通信参数
     * @param shadowDoc 影子文档
     * @param timestamp 更新时间戳
     * @author szh
     * @Date 2019/5/3 21:14
     */
    public void push(String deviceId, CommParams commParams, ShadowDoc shadowDoc, long timestamp) {
        RePayload rePayload = new RePayload(
                ShadowConst.PAYLOAD_STATUS_SUCCESS,
                shadowDoc.getDesiredStateTrans(),
                shadowDoc.getMetadata());

        assembleAndPublish(deviceId, ShadowConst.REPLY_METHOD_CONTROL, rePayload,
                commParams, timestamp, shadowDoc.getVersion());
    }

}
