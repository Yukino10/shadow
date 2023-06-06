package com.communication.mqtt;

import com.communication.common.DeviceAddr;

import java.net.InetSocketAddress;

/**
 * @Author: bin
 * @Date: 2019/11/1 8:45
 * @Description:
 */
public class InMQTTMessage {

    private DeviceAddr senderAddr;
    private String requestUrl;
    // data中包含 receiverDeviceId + data + requestUrl + uuid + isResp
    private byte[] data;
    private String uuid;
    private boolean isResp;


//    public InMQTTMessage() {
//    }

    public InMQTTMessage(DeviceAddr senderAddr, String requestUrl, byte[] data, String uuid) {
        this.senderAddr = senderAddr;
        this.requestUrl = requestUrl;
        this.data = data;
        this.uuid = uuid;
    }

    public DeviceAddr getSenderAddr() {
        return senderAddr;
    }

    public void setSenderAddr(DeviceAddr senderAddr) {
        this.senderAddr = senderAddr;
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

    public String getUuid() {
        return uuid;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
