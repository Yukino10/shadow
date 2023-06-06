package com.communication.mqtt;


import com.communication.coap.CoAPClientPool;
import com.communication.coap.CoAPMessageList;
import com.communication.coap.OutCoAPMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: bin
 * @Date: 2019/11/1 8:46
 * @Description:
 */
@Deprecated
public class MQTTMessageList {


    private static List<OutMQTTMessage> mQTTMessageList = new ArrayList<>();

    public static OutMQTTMessage getMQTTMessage() {
        synchronized (MQTTMessageList.class) {
            if (mQTTMessageList.size() > 0) {
                return mQTTMessageList.remove(0);
            }
            return null;
        }
    }

    public static void addMQTTMessage(OutMQTTMessage message) {
        synchronized (MQTTMessageList.class) {
            mQTTMessageList.add(message);
            // TODO
//            CoAPClientPool.trySendMessage();
        }
        // 添加完消息后，将暂停的线程启动(一个即可)。如果没有暂停的就算了
    }

    public static void addAllMQTTMessage(List<OutMQTTMessage> messages) {
        synchronized (MQTTMessageList.class) {
            mQTTMessageList.addAll(messages);
            // TODO
//            CoAPClientPool.trySendMessage();
        }
        // 添加完消息后，将暂停的线程启动(启动多个)。
        // 判断 list 的总长度，判断是否需要根据规则开启新线程
    }


    public static int getMQTTMessageAmount() {
        return mQTTMessageList.size();
    }
}
