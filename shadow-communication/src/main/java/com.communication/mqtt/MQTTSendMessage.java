package com.communication.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

/**
 * @Author: bin
 * @Date: 2019/11/9 11:05
 * @Description:
 */
public class MQTTSendMessage {
    public static String MQTT_MESSAGE_KEY_REQUEST_URL = "reqUrl";
    public static String MQTT_MESSAGE_KEY_SENDER_ID = "senderId";
    public static String MQTT_MESSAGE_KEY_DATA = "data";
    public static String MQTT_MESSAGE_KEY_UUID = "uuid";
    public static String MQTT_MESSAGE_KEY_SENDER_QUEUE = "senderQueue";

    // 消息请求的url
    String reqUrl;
    // 发送方的id
    String senderId;
    // 发送方一直箭头的 Queue
    String senderQueue;
    byte[] data;
    // 方便回调时查找
    String uuid;
    // 此消息是否为响应 默认不是
    private boolean isResp = false;

    // 一般初始化时，无法确定消息是否为响应
    public MQTTSendMessage(String reqUrl, String senderId, String senderQueue, byte[] data, String uuid) {
        this.reqUrl = reqUrl;
        this.senderId = senderId;
        this.senderQueue = senderQueue;
        this.data = data;
        this.uuid = uuid;
    }

    public MQTTSendMessage(String reqUrl, String senderId, String senderQueue, byte[] data, String uuid, boolean isResp) {
        this.reqUrl = reqUrl;
        this.senderId = senderId;
        this.senderQueue = senderQueue;
        this.data = data;
        this.uuid = uuid;
        this.isResp = isResp;
    }
    public boolean isResp() {
        return isResp;
    }




    //    public MQTTSendMessage(String reqUrl, String senderId, byte[] data) {
//        this.reqUrl = reqUrl;
//        this.senderId = senderId;
//        this.data = data;
//    }


    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderQueue() {
        return senderQueue;
    }

    public void setSenderQueue(String senderQueue) {
        this.senderQueue = senderQueue;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setResp(boolean resp) {
        isResp = resp;
    }

    @Override
    public String toString() {
        return "MQTTSendMessage{" +
                "reqUrl='" + reqUrl + '\'' +
                ", senderId='" + senderId + '\'' +
                ", senderQueue='" + senderQueue + '\'' +
                ", data=" + new String(data) +
                ", uuid='" + uuid + '\'' +
                ", isResp=" + isResp +
                '}';
    }
}