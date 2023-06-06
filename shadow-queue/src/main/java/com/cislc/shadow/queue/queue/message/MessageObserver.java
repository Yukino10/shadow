package com.cislc.shadow.queue.queue.message;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;

/**
 * MessageObserver
 * 消息观察者
 *
 * @since 2019/11/26 11:19
 * @author szh
 **/
public interface MessageObserver {

    /**
     * 消息到达时执行
     *
     * @param msg 消息
     * @author szh
     * @since 2019/11/26 13:33
     */
    void onMsgArrived(ShadowOpsBean msg);

}
