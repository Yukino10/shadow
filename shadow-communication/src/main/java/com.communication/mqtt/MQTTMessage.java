package com.communication.mqtt;

import java.net.InetSocketAddress;

/**
 * @Author: bin
 * @Date: 2019/11/1 8:45
 * @Description:
 *
 */
@Deprecated
public class MQTTMessage {



//    // 发送方的ip+端口
//    private InetSocketAddress senderAddr;
//    // 接收方的ip+端口
//    private InetSocketAddress receiverAddr;

    // ip + Queue + requestUrl

    private byte[] data;
    // 请求的url
    private String requestUrl;

    /**
     * MQTT发送消息时，将普通消息的的requestUrl、uuid和data 转化位一个byte[] 后使用 mqtt发送
     * 如果有返回值，根据uuid监听相应的队列获取响应结果。
     * 例： a->b 发送 请求 "give me a message." uuid 为："778899" requestUrl为 "/message/back" 此请求进入b的默认监听队列
     * b处理后，向 ba778899 队列发送响应结果 "this is a message." uuid 为 "778899" requestUrl 为 空
     *
     * 接收到消息后，再反向解析
     */
    /**
     * 根据
     * @param data
     */
    public static MQTTMessage transBytesToMQTTMessage(byte[] data) {

        return new MQTTMessage();
    }

    /**
     *
     * @param message
     * @return
     */
    public static byte[] transMQTTMessageToBytes(MQTTMessage message) {

        return new byte[1];
    }
}
