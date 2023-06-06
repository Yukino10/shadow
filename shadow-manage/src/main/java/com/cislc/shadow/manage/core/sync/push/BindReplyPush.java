package com.cislc.shadow.manage.core.sync.push;

import com.cislc.shadow.manage.core.bean.comm.RePayload;
import com.cislc.shadow.manage.core.bean.comm.ReState;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.bean.field.ShadowField;
import com.cislc.shadow.manage.core.bean.shadow.ShadowDesiredDoc;
import com.cislc.shadow.utils.bean.comm.CommParams;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BindReplyPush
 * @Description 服务器下发绑定信息
 * @author szh
 * @Date 2020/1/28 17:03
 **/
public class BindReplyPush extends MessagePush {

    /**
     * @Description 下发绑定数据
     * @param className 类名
     * @param bindCode 绑定标识符
     * @param deviceId 设备id
     * @param sri sir
     * @param commParams 通信参数
     * @author szh
     * @Date 2020/1/28 19:16
     */
    public void push(String className, String bindCode, String deviceId, String sri, CommParams commParams) {
        // 回复绑定标识符、设备id、sri
        Map<String, Object> field = new HashMap<>();
        field.put(ShadowConst.BIND_CODE, bindCode);
        field.put(ShadowConst.DEVICE_ID, deviceId);
        field.put("sri", sri);
        // 封装回复数据
        ShadowField shadowField = new ShadowField(className, sri, field);
        ShadowDesiredDoc desired = new ShadowDesiredDoc();
        desired.setUpdate(Collections.singletonList(shadowField));
        ReState state = new ReState(desired);
        RePayload payload = new RePayload(ShadowConst.PAYLOAD_STATUS_SUCCESS, state);
        // 发送
        assembleAndPublish(deviceId, ShadowConst.OPERATION_METHOD_BIND, payload, commParams, System.currentTimeMillis(), 1);
    }

}
