package com.cislc.shadow.queue.queue.message;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;
import org.springframework.stereotype.Component;

/**
 * 设备删除消息处理
 *
 * @author szh
 * @since 2021/5/11 20:53
 **/
@Component
public abstract class AbsDeleteMsgHandler<T extends ShadowEntity> {

    /**
     * 处理设备删除逻辑
     *
     * @param opsBean 通信操作内容
     * @author szh
     * @since 2021/5/11 21:00
     */
    @SuppressWarnings("unchecked")
    public final void handleMsg(ShadowOpsBean opsBean) {
        ShadowBean shadowBean = ShadowFactory.getShadowBean(opsBean.getDeviceId());
        handleDelMsg((T) shadowBean.getData());
    }

    /**
     * 数据删除操作
     *
     * @param device 设备实体
     * @author szh
     * @since 2021/5/11 21:00
     */
    public abstract void handleDelMsg(T device);

}
