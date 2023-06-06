package com.communication.common;

import com.communication.coap.*;
import com.communication.mqtt.MQTTClientPool;
import com.communication.mqtt.MQTTSendMessage;
import com.communication.mqtt.OutMQTTMessage;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * @Author: bin
 * @Date: 2019/10/31 14:21
 * @Description:
 * 构造函数只是简单的组装方法，使用线程池的send方法进行发送
 */
public class OutMessage {
    // 要走的协议 不为空
    private Protocol protocol;
    // 消息接收方地址信息 如果为response则为空
    private DeviceAddr receiverAddr;
    // 此处 data 与 requestUrl 分开， 在特定协议的消息中再合并至 byte[]
    private byte[] data;
    // 请求的端口
    private String requestUrl;

    private ShadowCallBack callBack;

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public DeviceAddr getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(DeviceAddr receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public ShadowCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ShadowCallBack callBack) {
        this.callBack = callBack;
    }


    // 消息的初始化方法 // 必须告诉server用哪个方法处理请求。
//    public OutMessage(Protocol protocol, byte[] data, InetSocketAddress receiverAddr) {
//        switch (protocol) {
//            case COAP:
//                coAPMessage = new OutCoAPMessage(data, receiverAddr);
//                CoAPMessageList.addCoAPMessage(coAPMessage);
//                return;
//            case DTLS:
//            default:
//                return;
//        }
//    }


    Protocol getProtocol() {
        return protocol;
    }


    /**
     * 带回调的指定协议发送方式 未完成
     * @param protocol
     * @param receiverAddr
     * @param data
     * @param requestUrl
     * @param callBack
     */
    public OutMessage(Protocol protocol, DeviceAddr receiverAddr, byte[] data, String requestUrl, ShadowCallBack callBack) {
        this.protocol = protocol;
        this.receiverAddr = receiverAddr;
        this.data = data;
        this.requestUrl = requestUrl;
        this.callBack = callBack;
    }

    /**
     * 用于别的设备发起请求，我方只是响应
     * @param protocol
     * @param data
     */
    public OutMessage(Protocol protocol, DeviceAddr receiverAddr, byte[] data) {
        this.protocol = protocol;
        this.receiverAddr = receiverAddr;
        this.data = data;
    }

    /**
     * 用于主动发起请求
     * @param protocol
     * @param receiverAddr
     * @param data
     * @param requestUrl
     */
    public OutMessage(Protocol protocol, DeviceAddr receiverAddr, byte[] data, String requestUrl) {
        this.protocol = protocol;
        this.receiverAddr = receiverAddr;
        this.data = data;
        this.requestUrl = requestUrl;
    }

    public OutMessage(DeviceAddr receiverAddr, byte[] data, String requestUrl) {
        this.receiverAddr = receiverAddr;
        this.data = data;
        this.requestUrl = requestUrl;
    }

    /**
     * 带回调的默认协议发送方式 未完成
     * @param receiverAddr
     * @param data
     * @param requestUrl
     * @param callBack
     */
    public OutMessage(DeviceAddr receiverAddr, byte[] data, String requestUrl, ShadowCallBack callBack) {
        this.receiverAddr = receiverAddr;
        this.data = data;
        this.requestUrl = requestUrl;
        this.callBack = callBack;
    }

    /**
     * TODO 必须定义send方法 因为，有时是response。。。
     * 主动调用此方法的都是用户主动发送， 服务端响应时不走此方法，因为还要封装其他数据
     */
    public void send(){

        switch (protocol) {
            case COAP:
                // 尝试使用coap发送
                if (receiverAddr.coAPAddress == null) {
                    // 检查接收设备是否可以使用此协议
                    System.out.println("error, 此设备没有配置coap地址，无法通信");
                } else if (1 == 2) {
                    // TODO 检查发送设备是否可以使用此协议
                } else if (callBack != null) {
                    // 发送消息。
                    CoAPClientPool.trySendMessage(MessageTransUtil.transOutMessageToCoAP(this), this.callBack);
                } else {
                    CoAPClientPool.trySendMessage(MessageTransUtil.transOutMessageToCoAP(this));
                }
                return;
            case MQTT:
                if (receiverAddr.mqttAddress == null) {
                    // 检查接收设备是否可以使用此协议
                    System.out.println("error, 此设备没有配置 mqtt 地址，无法通信");
                } else if (1 == 2) {
                    // TODO 检查发送设备是否可以使用此协议
                } else {
                    // 发送消息，如果有回调 执行回调
                    OutMQTTMessage outMQTTMessage = MessageTransUtil.transOutMessageToMQTT(this);
                    MQTTSendMessage mqttSendMessage = MessageTransUtil.transOutMQTTMessageToMQTTSendMessage(outMQTTMessage, false);
                    // 重新封装要发送的数据
                    outMQTTMessage.setData(MessageTransUtil.getSendData(mqttSendMessage));
                    MQTTClientPool.trySendMessage(outMQTTMessage);
                }
                return;
            default:
        }
    }

    @Override
    public String toString() {
        return "OutMessage{" +
                "protocol=" + protocol +
                ", receiverAddr=" + receiverAddr +
                ", data=" + Arrays.toString(data) +
                ", requestUrl='" + requestUrl + '\'' +
                ", callBack=" + callBack +
                '}';
    }
}
