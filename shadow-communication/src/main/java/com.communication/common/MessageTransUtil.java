package com.communication.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.communication.coap.InCoAPMessage;
import com.communication.coap.OutCoAPMessage;
import com.communication.mqtt.InMQTTMessage;
import com.communication.mqtt.MQTTSendMessage;
import com.communication.mqtt.OutMQTTMessage;
import com.communication.utils.CommonUtil;


import static com.communication.mqtt.MQTTSendMessage.*;

/**
 * @Author: bin
 * @Date: 2019/11/8 12:49
 * @Description:
 * 约定 {@link InCoAPMessage } {@link OutCoAPMessage} {@link InMQTTMessage } {@link OutMQTTMessage}
 * 中的 data[] 或 body[] 都是最终使用协议发送的消息。 其中封装了除用户数据以外的其他数据。
 * 只有 {@link InMessage} {@link OutMessage} 中的 data[] 或 body[] 才是用户发送的数据。
 */
public class MessageTransUtil {

    public static InMessage transCoAPToInMessage(InCoAPMessage inCoAPMessage) {
        return new InMessage(Protocol.COAP, inCoAPMessage.getData());
    }

    /**
     * 除了返回信息，还会返回 requestUrl
     * @param inMQTTMessage
     * @return
     */
    public static InMessage transMQTTToInMessage(InMQTTMessage inMQTTMessage) {

        return new InMessage(Protocol.MQTT, inMQTTMessage.getSenderAddr(), inMQTTMessage.getData());
    }

    public static OutCoAPMessage transOutMessageToCoAP(OutMessage outMessage) {
        return new OutCoAPMessage(outMessage.getReceiverAddr(), outMessage.getRequestUrl(), outMessage.getData(), outMessage.getCallBack());
    }

    /**
     * 用户主动发起请求时，调用的转换方法，不含系统回调
     * @param outMessage
     * @return
     */
    public static OutMQTTMessage transOutMessageToMQTT(OutMessage outMessage) {
        OutMQTTMessage result = new OutMQTTMessage(outMessage.getReceiverAddr(), CommonUtil.getCurrentDeviceAddr(),
                outMessage.getRequestUrl(), outMessage.getData(), outMessage.getCallBack());
        // 生成uuid
        result.setUuid(CommonUtil.createUuid());
        // TODO
        return result;
    }

    public static MQTTSendMessage transOutMQTTMessageToMQTTSendMessage(OutMQTTMessage outMQTTMessage, boolean  isResp) {
        DeviceAddr currentAddr = CommonUtil.getCurrentDeviceAddr();
        return new MQTTSendMessage(outMQTTMessage.getRequestUrl(), currentAddr.getDeviceId(), currentAddr.getMqttAddress().getQueueName(), outMQTTMessage.getData(), outMQTTMessage.getUuid(), isResp);
    }






    /**
     * mqtt server 在接收到消息后，首先执行此方法进行解析，才能确定消息请求的接口等信息
     * @param inMQTTBytes
     * @return
     */
    public static InMQTTMessage bytesToInMQTTMessage(byte[] inMQTTBytes) {
        JSONObject jsonObject = JSONObject.parseObject(new String(inMQTTBytes));

        // 组织发送方的id信息
        DeviceAddr senderAddr = new DeviceAddr();
        senderAddr.setDeviceId(jsonObject.getString(MQTT_MESSAGE_KEY_SENDER_ID));

        return new InMQTTMessage(senderAddr, jsonObject.getString(MQTT_MESSAGE_KEY_REQUEST_URL),
                jsonObject.getBytes(MQTT_MESSAGE_KEY_DATA), jsonObject.getString(MQTT_MESSAGE_KEY_UUID));
    }

    /**
     * 将 MQTT 接收到的 byte[] 转为 {@link MQTTSendMessage} 为重定向或回调做准备
     * @param inMQTTBytes
     * @return
     */
    public static MQTTSendMessage parseInMQTTBytes(byte[] inMQTTBytes) {
        JSONObject jsonObject = JSONObject.parseObject(new String(inMQTTBytes));

        return new MQTTSendMessage(jsonObject.getString(MQTT_MESSAGE_KEY_REQUEST_URL),
                jsonObject.getString(MQTT_MESSAGE_KEY_SENDER_ID), jsonObject.getString(MQTT_MESSAGE_KEY_SENDER_QUEUE),
                jsonObject.getBytes(MQTT_MESSAGE_KEY_DATA), jsonObject.getString(MQTT_MESSAGE_KEY_UUID));
    }

    /**
     * 发送消息时，使用此方法格式化数据，并装入mqtt 消息体中
     * @return
     */
    public static byte[] getSendData(MQTTSendMessage message) {
        System.out.println(JSONObject.toJSONString(message));
        return JSONObject.toJSONString(message).getBytes();
    }
    /**
     * 将byte[]格式的实体，通过反射机制转为实体。
     * 要求实体必须具有空的构造函数，每个属性有标准的set方法。
     * @param inMQTTBytes
     * @return
     */
//    public static InMQTTMessage bytesToMQTTMessage(byte[] inMQTTBytes) {
//
//        JSONObject jsonObject = JSONObject.parseObject(new String(inMQTTBytes));
//        InMQTTMessage inMQTTMessage = new InMQTTMessage();
//        for (Field field : InMQTTMessage.class.getDeclaredFields()) {
//            for ()
//        }
//
//    }

    /**
     * mqtt client 在发送消息之前、或 server在回复消息之前 需要将信息进行组装，然后才能发送。
     * @param outMQTTMessage
     * @return
     */
    public static byte[] outMQTTMessageToBytes(OutMQTTMessage outMQTTMessage) {

        MQTTSendMessage mqttSendMessage = new MQTTSendMessage(outMQTTMessage.getRequestUrl(),
                outMQTTMessage.getReceiverAddr().getDeviceId(), outMQTTMessage.getReceiverAddr().getMqttAddress().getQueueName(),
                outMQTTMessage.getData(), outMQTTMessage.getUuid());
        return JSON.toJSONString(mqttSendMessage).getBytes();
    }


}
