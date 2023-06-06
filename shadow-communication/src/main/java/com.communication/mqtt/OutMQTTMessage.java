package com.communication.mqtt;

import com.communication.common.DeviceAddr;
import com.communication.common.ShadowCallBack;

/**
 * @Author: bin
 * @Date: 2019/11/1 8:45
 * @Description:
 */
public class OutMQTTMessage {
    private DeviceAddr receiverAddr;
    private DeviceAddr thisAddr;
    private String requestUrl;
    // data中包含 receiverDeviceId + data + requestUrl
    private byte[] data;
    private ShadowCallBack callback;
    // 方便回调时查找
    private String uuid;
    // 此消息是否为响应
    private boolean isResp;

    /**
     * 回复请求时，使用此构造函数 有回调
     * @param receiverAddr
     * @param thisAddr
     * @param data
     * @param callback
     */
    public OutMQTTMessage(DeviceAddr receiverAddr, DeviceAddr thisAddr, byte[] data, ShadowCallBack callback) {
        this.receiverAddr = receiverAddr;
        this.thisAddr = thisAddr;
        this.data = data;
        this.callback = callback;
    }


    /**
     * 回复请求时，使用此构造函数 无回调
     * @param receiverAddr
     * @param thisAddr
     * @param data
     */
    public OutMQTTMessage(DeviceAddr receiverAddr, DeviceAddr thisAddr, byte[] data) {
        this.receiverAddr = receiverAddr;
        this.thisAddr = thisAddr;
        this.data = data;
    }

    /**
     * 发起请求时，使用此构造函数 有回调
     * @param receiverAddr
     * @param thisAddr
     * @param requestUrl
     * @param data
     * @param callback
     */
    public OutMQTTMessage(DeviceAddr receiverAddr, DeviceAddr thisAddr, String requestUrl, byte[] data, ShadowCallBack callback) {
        this.receiverAddr = receiverAddr;
        this.thisAddr = thisAddr;
        this.requestUrl = requestUrl;
        this.data = data;
        this.callback = callback;
    }
    /**
     * 发起请求时，使用此构造函数 无回调
     * @param receiverAddr
     * @param thisAddr
     * @param requestUrl
     * @param data
     */
    public OutMQTTMessage(DeviceAddr receiverAddr, DeviceAddr thisAddr, String requestUrl, byte[] data) {
        this.receiverAddr = receiverAddr;
        this.thisAddr = thisAddr;
        this.requestUrl = requestUrl;
        this.data = data;
    }

    public DeviceAddr getReceiverAddr() {
        return receiverAddr;
    }

    public void setReceiverAddr(DeviceAddr receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public ShadowCallBack getCallback() {
        return callback;
    }

    public void setCallback(ShadowCallBack callback) {
        this.callback = callback;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void preSend() {

    }
}
