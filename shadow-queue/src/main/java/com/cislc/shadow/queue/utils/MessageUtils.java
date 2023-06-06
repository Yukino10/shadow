package com.cislc.shadow.queue.utils;

import com.cislc.shadow.queue.queue.QueueExecutor;
import com.cislc.shadow.queue.queue.message.MessageObserver;

/**
 * MessageUtils
 * 消息处理工具
 *
 * @since 2019/11/26 13:34
 * @author szh
 **/
public class MessageUtils {

    /**
     * 增加设备数据观察者
     *
     * @param observer 观察者
     * @author szh
     * @since 2019/11/26 13:39
     */
    public static void addMsgObserver(MessageObserver observer) {
        QueueExecutor executor = ApplicationContextUtil.getBean(QueueExecutor.class);
        executor.addObserver(observer);
    }

    /**
     * 删除设备数据观察者
     *
     * @param observer 观察者
     * @author szh
     * @since 2019/11/26 13:39
     */
    public static void delMsgObserver(MessageObserver observer) {
        QueueExecutor executor = ApplicationContextUtil.getBean(QueueExecutor.class);
        executor.delObserver(observer);
    }

}
