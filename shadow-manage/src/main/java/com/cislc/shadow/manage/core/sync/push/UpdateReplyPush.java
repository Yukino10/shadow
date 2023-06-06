package com.cislc.shadow.manage.core.sync.push;

import com.cislc.shadow.manage.core.bean.comm.ReContent;
import com.cislc.shadow.manage.core.bean.comm.RePayload;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.enums.ReErrorCode;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * @ClassName UpdateReplyPush
 * @Description 更新结果回复推送
 * @Date 2019/5/3 17:30
 * @author szh
 **/
public class UpdateReplyPush extends MessagePush {

    /**
     * @Description 更新成功后的推送
     * @param deviceId 设备id
     * @param commParams 通信参数
     * @param version 影子版本
     * @author szh
     * @Date 2019/5/3 17:52
     */
    public void pushSuccess(String deviceId, CommParams commParams, int version) {
        RePayload rePayload = new RePayload(ShadowConst.PAYLOAD_STATUS_SUCCESS, version);
        assembleAndPublish(deviceId, ShadowConst.REPLY_METHOD_REPLY, rePayload,
                commParams, System.currentTimeMillis(), version);
    }

    /**
     * @Description 更新失败后的推送
     * @param deviceId 设备id
     * @param commParams 通信参数
     * @param errorCode 失败原因
     * @author szh
     * @Date 2019/5/3 18:35
     */
    public void pushError(String deviceId, CommParams commParams, ReErrorCode errorCode) {
        ReContent reContent = new ReContent(errorCode);
        RePayload rePayload = new RePayload(ShadowConst.PAYLOAD_STATUS_ERROR, reContent);
        assembleAndPublish(deviceId, ShadowConst.REPLY_METHOD_REPLY, rePayload,
                commParams, System.currentTimeMillis(), null);
    }

}
