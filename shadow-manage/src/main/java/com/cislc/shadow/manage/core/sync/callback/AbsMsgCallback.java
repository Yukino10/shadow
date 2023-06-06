package com.cislc.shadow.manage.core.sync.callback;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;

/**
 * @ClassName AbsMsgCallback
 * @Description mqtt回调抽象类
 * @Date 2019/4/29 11:11
 * @author szh
 **/
public abstract class AbsMsgCallback {

    /**
     * @Description 回调方法
     * @param deviceId 设备Id
     * @param opsBean 返回的消息
     * @author szh
     * @Date 2019/4/29 16:28
     */
    public void run(String deviceId, ShadowOpsBean opsBean) {
        // 取出容器中的对象
        ShadowBean shadowBean = ShadowFactory.getShadowBean(deviceId);
        if (null != shadowBean) {
            // 处理逻辑
            dealMessage(opsBean, shadowBean);
        }
    }

    /**
     * @Description ShadowOpsBean
     * @param opsBean 返回的消息
     * @param shadowBean 内存中的影子
     * @author szh
     * @Date 2019/4/29 16:37
     */
    public abstract void dealMessage(ShadowOpsBean opsBean, ShadowBean shadowBean);

}
