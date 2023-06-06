package com.communication.common;


/**
 * @Author: bin
 * @Date: 2019/10/31 14:21
 * @Description:
 */
public class InMessage {

    // 要走的协议 不为空
    private Protocol protocol;
    // 消息发送方地址信息 如果为response则为空
    private DeviceAddr senderAddr;
    // 此处 data 与 requestUrl 分开， 在特定协议的消息中再合并至 byte[]
    private byte[] data;

    public InMessage(Protocol protocol, byte[] data) {
        this.protocol = protocol;
        this.data = data;
    }
    public InMessage(Protocol protocol, DeviceAddr senderAddr, byte[] data) {
        this.protocol = protocol;
        this.senderAddr = senderAddr;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public DeviceAddr getSenderAddr() {
        return senderAddr;
    }

    public void setSenderAddr(DeviceAddr senderAddr) {
        this.senderAddr = senderAddr;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


}
