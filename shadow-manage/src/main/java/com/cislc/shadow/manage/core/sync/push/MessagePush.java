package com.cislc.shadow.manage.core.sync.push;

import com.cislc.shadow.manage.core.bean.comm.RePayload;
import com.cislc.shadow.manage.core.bean.comm.ShadowReplyBean;
import com.cislc.shadow.manage.core.exception.NoIpException;
import com.cislc.shadow.utils.bean.comm.CommParams;
import com.cislc.shadow.utils.comm.Sender;
import com.cislc.shadow.utils.enums.Protocol;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName MessagePush
 * @Description 消息回复
 * @Date 2019/5/3 18:30
 * @author szh
 **/
class MessagePush {

    /**
     * @Description 组织数据并推送
     * @param deviceId 设备id
     * @param method 推送方法
     * @param rePayload 信息载荷
     * @param commParams 通信参数
     * @param timestamp 信息时间戳
     * @param version 信息版本
     * @author szh
     * @Date 2019/5/3 21:16
     */
    void assembleAndPublish(String deviceId, String method, RePayload rePayload, CommParams commParams, long timestamp, Integer version) {
        ShadowReplyBean replyBean = new ShadowReplyBean();
        replyBean.setDeviceId(deviceId);
        replyBean.setMethod(method);
        replyBean.setPayload(rePayload);
        replyBean.setTimestamp(timestamp);
        if (null != version) {
            replyBean.setVersion(version);
        }
        if (Protocol.COAP.equals(commParams.getProtocol()) && StringUtils.isEmpty(commParams.getIp())) {
            throw new NoIpException();
        }
        Sender.sendMsg(commParams, replyBean.toString());
    }

}
