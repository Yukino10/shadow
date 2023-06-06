package com.cislc.shadow.manage.core.sync.callback;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.sync.push.GetReplyPush;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * @ClassName ShadowGetCallback
 * @Description 获取影子的回调
 * @Date 2019/4/29 16:48
 * @author szh
 **/
public class ShadowGetCallback extends AbsMsgCallback {

    private final GetReplyPush getReplyPush;

    public ShadowGetCallback() {
        this.getReplyPush = new GetReplyPush();
    }

    @Override
    public void dealMessage(ShadowOpsBean opsBean, ShadowBean shadowBean) {
        shadowBean.updateTimestamp();
        CommParams commParams = new CommParams(shadowBean.getProtocol(), shadowBean.getIp(),
                shadowBean.getTopic(), shadowBean.getEncryption(), shadowBean.getDeviceId());
        getReplyPush.push(shadowBean.getDeviceId(), commParams, shadowBean.getDoc(), shadowBean.getData().getClass());
    }

}
