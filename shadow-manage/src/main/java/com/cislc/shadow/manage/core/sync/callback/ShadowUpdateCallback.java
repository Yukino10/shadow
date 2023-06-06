package com.cislc.shadow.manage.core.sync.callback;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowDesiredDoc;
import com.cislc.shadow.manage.core.bean.shadow.ShadowDoc;
import com.cislc.shadow.manage.core.enums.ReErrorCode;
import com.cislc.shadow.manage.core.sync.push.UpdateReplyPush;
import com.cislc.shadow.utils.bean.comm.CommParams;

/**
 * ShadowUpdateCallback
 * 更新影子的回调
 * @since 2019/4/29 16:39
 * @author szh
 **/
public class ShadowUpdateCallback extends AbsMsgCallback {

    private final UpdateReplyPush updateReplyPush;

    public ShadowUpdateCallback() {
        this.updateReplyPush = new UpdateReplyPush();
    }

    @Override
    public void dealMessage(ShadowOpsBean opsBean, ShadowBean shadowBean) {
        int deviceVer = opsBean.getVersion();
        int shadowVer = shadowBean.getDoc().getVersion();
        CommParams commParams = new CommParams(shadowBean.getProtocol(), shadowBean.getIp(),
                shadowBean.getTopic(), shadowBean.getEncryption(), shadowBean.getDeviceId());

        // TODO 删除版本号判断
//        if (deviceVer > 0 && deviceVer <= shadowVer) { // 版本小于1时强制更新
//            // 推送版本错误信息
//            updateReplyPush.pushError(shadowBean.getDeviceId(), commParams, ReErrorCode.VERSION_CONFLICT);
//            return;
//        }

        ShadowDoc shadowDoc = shadowBean.getDoc();
        // 设备状态更新成功，清除desired
        if (null == opsBean.getState().getDesired()) {
            ReErrorCode error = shadowBean.clearDesired();
            if (null != error) {
                // 推送写锁错误
                updateReplyPush.pushError(shadowBean.getDeviceId(), commParams, error);
                return;
            }
        }
        // 更新影子
        ShadowDesiredDoc reportedValue = opsBean.getState().getReported();
        if (null != reportedValue) {
            // 更新影子属性
            ReErrorCode error = shadowBean.updateShadowByDevice(reportedValue);
            if (null != error) {
                // 推送属性错误信息
                updateReplyPush.pushError(shadowBean.getDeviceId(), commParams, error);
                return;
            }
        }
        // 更新版本号
        int nextVer = deviceVer > 0 ? deviceVer : shadowVer + 1;
        shadowDoc.setVersion(nextVer);
        // 如果更新了ip在这里重置
        commParams.setIp(shadowBean.getIp());
        // 推送成功信息
        updateReplyPush.pushSuccess(shadowBean.getDeviceId(), commParams, nextVer);
    }

}
