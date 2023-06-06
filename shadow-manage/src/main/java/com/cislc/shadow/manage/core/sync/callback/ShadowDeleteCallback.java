package com.cislc.shadow.manage.core.sync.callback;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;

/**
 * @ClassName ShadowDeleteCallback
 * @Description 删除影子回调
 * @Date 2019/4/29 16:49
 * @author szh
 **/
public class ShadowDeleteCallback extends AbsMsgCallback {

    @Override
    public void dealMessage(ShadowOpsBean opsBean, ShadowBean shadowBean) {
        shadowBean.updateTimestamp();
    }

}
