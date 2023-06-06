package com.cislc.shadow.manage.core.sync.callback;

import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;
import com.cislc.shadow.manage.core.sync.push.BindReplyPush;
import com.cislc.shadow.utils.bean.comm.CommParams;
import com.cislc.shadow.utils.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName ShadowBindCallback
 * @Description 设备绑定回调
 * @author szh
 * @Date 2020/1/28 17:00
 **/
@Slf4j
public class ShadowBindCallback extends AbsMsgCallback {

    private final BindReplyPush bindReplyPush;

    public ShadowBindCallback() {
        this.bindReplyPush = new BindReplyPush();
    }

    /**
     * @Description 获取deviceId
     * @param opsBean 操作消息
     * @author szh
     * @Date 2020/1/28 17:29
     */
    public void run(ShadowOpsBean opsBean) {
        try {
            String bindCode = (String) opsBean.getState().getReported().getUpdate().get(0)
                    .getField().get(ShadowConst.BIND_CODE);
            if (StringUtils.isEmpty(bindCode)) {
                log.error("bind code is null");
                return;
            }
            String deviceId = ShadowFactory.getDeviceIdByBindCode(bindCode);
            if (StringUtils.isEmpty(deviceId)) {
                log.error("device with bind code {} not exist", bindCode);
                return;
            }
            run(deviceId, opsBean);
        } catch (NullPointerException e) {
            log.error("message is null");
        }
    }

    @Override
    public void dealMessage(ShadowOpsBean opsBean, ShadowBean shadowBean) {
        // 保存ip
        String ip = (String) opsBean.getState().getReported().getUpdate().get(0).getField().get(ShadowConst.IP);
        if (Protocol.COAP.equals(shadowBean.getProtocol())) {
            // 使用coap协议的绑定时必须上报ip
            if (StringUtils.isEmpty(ip)) {
                return;
            } else {
                shadowBean.setIp(ip);
            }
        }
        // 删除map中的code
        String bindCode = shadowBean.getData().getBindCode();
        ShadowFactory.removeBindCode(bindCode);
        // 删除影子中的code
        shadowBean.clearBindCode();
        // 保存到数据库
        shadowBean.saveData();

        // 回复
        CommParams commParams = new CommParams(shadowBean.getProtocol(), shadowBean.getIp(),
                ShadowConst.BIND_TOPIC, shadowBean.getEncryption(), shadowBean.getDeviceId());
        bindReplyPush.push(shadowBean.getData().getClass().getSimpleName(), bindCode,
                shadowBean.getDeviceId(), shadowBean.getData().getSri(), commParams);
    }

}
