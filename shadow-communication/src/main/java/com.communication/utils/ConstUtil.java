package com.communication.utils;

/**
 * @Author: bin
 * @Date: 2019/9/28 10:50
 * @Description:
 */
public class ConstUtil {

    /** 用于指明通信的协议*/
    public static final byte COAP = 1;
    public static final byte MQTT = 2;
    public static final byte HTTP = 3;

    /** 暂时考虑在handler处理消息时，必须交换消息的发送方和接收方信息。*/
    // 1、发送者只有client，必须从server返回
    // 2、对方含有回调函数，需要返回结果
    // 3、本机含有回调函数
    public static final byte MESSAGE_NEED_RESPONSE = 1;
    public static final byte MESSAGE_HIS_CALLBACK = 2;
    public static final byte MESSAGE_MY_CALLBACK = 3;
    public static final byte MESSAGE_NO_CALLBACK = 4;
    /** 用于指明消息的为帧还是*/


}
