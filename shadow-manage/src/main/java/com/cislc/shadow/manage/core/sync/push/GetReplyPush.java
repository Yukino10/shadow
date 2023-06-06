package com.cislc.shadow.manage.core.sync.push;

import com.cislc.shadow.manage.core.bean.comm.RePayload;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.bean.shadow.ShadowDoc;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * @ClassName GetReplyPush
 * @Description 设备主动获取状态回复
 * @Date 2019/5/3 21:21
 * @author szh
 **/
public class GetReplyPush extends MessagePush {

    /**
     * @Description 推送属性
     * @param deviceId 设备id
     * @param commParams 通信参数
     * @param shadowDoc 影子文档
     * @param dataClass 影子对象类型
     * @author szh
     * @Date 2019/5/3 21:14
     */
    public void push(String deviceId, CommParams commParams, ShadowDoc shadowDoc, Class<?> dataClass) {
        RePayload rePayload = new RePayload(ShadowConst.PAYLOAD_STATUS_SUCCESS,
                shadowDoc.getAllStateTrans(dataClass), shadowDoc.getMetadata());
        assembleAndPublish(deviceId, ShadowConst.REPLY_METHOD_REPLY, rePayload,
                commParams, System.currentTimeMillis(), shadowDoc.getVersion());
    }

}
