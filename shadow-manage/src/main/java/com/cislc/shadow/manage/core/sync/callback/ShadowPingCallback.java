package com.cislc.shadow.manage.core.sync.callback;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.sync.push.PingReplyPush;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * 心跳回调
 *
 * @author szh
 * @since 2020/12/25 11:30
 **/
public class ShadowPingCallback extends AbsMsgCallback {

    private final PingReplyPush pingReplyPush;

    public ShadowPingCallback() {
        this.pingReplyPush = new PingReplyPush();
    }

    @Override
    public void dealMessage(ShadowOpsBean opsBean, ShadowBean shadowBean) {
        shadowBean.updateTimestamp();

        CommParams commParams = new CommParams(shadowBean.getProtocol(), shadowBean.getIp(),
                shadowBean.getTopic(), shadowBean.getEncryption(), shadowBean.getDeviceId());
        int shadowVer = shadowBean.getDoc().getVersion();
        pingReplyPush.push(shadowBean.getDeviceId(), commParams, shadowVer);
    }

}
