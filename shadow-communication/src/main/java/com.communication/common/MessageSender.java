package com.communication.common;

import com.communication.coap.CoAPClientPool;
import com.communication.coap.CoAPMessageList;
import com.communication.mqtt.MQTTClientPool;
import com.communication.mqtt.MQTTMessageList;

import static com.communication.common.Protocol.COAP;
import static com.communication.common.Protocol.MQTT;

/**
 * @Author: bin
 * @Date: 2019/11/4 10:13
 * @Description:
 */
@Deprecated
public class MessageSender {

    /**
     * 交给用户使用
     * 多线程发送，不会返回发送结果
     * @param outMessage
     */
    public static void send(OutMessage outMessage) {
        switch (outMessage.getProtocol()) {
            case COAP:
                CoAPMessageList.addCoAPMessage(MessageTransUtil.transOutMessageToCoAP(outMessage));
                return;
            case DTLS:
//                MQTTMessageList.addMQTTMessage(MessageTransUtil.transOutMessageToMQTT(outMessage));
                return;
            default:
                return;
        }
    }
}
