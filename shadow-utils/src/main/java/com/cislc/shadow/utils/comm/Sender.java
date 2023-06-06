package com.cislc.shadow.utils.comm;

import com.cislc.shadow.utils.bean.comm.CommParams;
import com.cislc.shadow.utils.mqtt.TopicUtils;
import com.cislc.shadow.utils.mqtts.MqttFactory;
import com.cislc.shadow.utils.coap.CoAPClient;
import com.cislc.shadow.utils.encription.AESUtils;
import com.cislc.shadow.utils.encription.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName Sender
 * @Description 消息发送者
 * @Date 2019/11/25 10:38
 * @author szh
 **/
public class Sender {

    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    /**
     * @Description 发送消息
     * @param commParams 消息参数
     * @param msg 消息内容
     * @author szh
     * @Date 2019/11/25 10:41
     */
    public static void sendMsg(CommParams commParams, String msg) {
        // 加密
        try {
            switch (commParams.getEncryption()) {
                case AES:
                    msg = AESUtils.aesEncryptString(msg, commParams.getEncryptKey());
                    break;

                case RSA:
                    msg = RSAUtil.encrypt(msg);
                    break;
            }
        } catch (Exception ex) {
            log.error("发送消息失败：加密失败", ex);
            return;
        }
        
        // 发送
        switch (commParams.getProtocol()) {
            case COAP:
                CoAPClient.sendMessage(commParams.getIp(), msg, System.out::println);
                break;
            case MQTT:
                // mqtt发送消息
                String topic = TopicUtils.getGetTopic(commParams.getTopic());
                MqttFactory.publish(0,false, topic, msg);
                break;
        }
    }

}
