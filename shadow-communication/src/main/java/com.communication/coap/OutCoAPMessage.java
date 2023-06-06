package com.communication.coap;


import com.communication.common.CommunicationService;
import com.communication.common.DeviceAddr;
import com.communication.common.ShadowCallBack;

import java.net.InetSocketAddress;

/**
 * @Author: bin
 * @Date: 2019/10/23 13:15
 * @Description:
 * 使用 {@link CommunicationService } 标注的 方法的出参
 */
public class OutCoAPMessage {

    private DeviceAddr receiverAddr;
    private String requestUrl;
    private byte[] data;
    private ShadowCallBack callback;

    /**
     * 用于response时，只包含数据，对方收到后执行回调
     * @param data
     */
    public OutCoAPMessage(byte[] data) {
        this.data = data;
    }

    /**
     * 用于只发送请求，对方处理后没有反馈的情况
     * @param receiverAddr
     * @param requestUrl
     * @param data
     */
    public OutCoAPMessage(DeviceAddr receiverAddr, String requestUrl, byte[] data) {
        this.receiverAddr = receiverAddr;
        this.requestUrl = requestUrl;
        this.data = data;
    }

    /**
     * 向对方发起请求，对方处理后给予响应，我方执行回调
     * @param receiverAddr
     * @param requestUrl
     * @param data
     * @param callback
     */
    public OutCoAPMessage(DeviceAddr receiverAddr, String requestUrl, byte[] data, ShadowCallBack callback) {
        this.receiverAddr = receiverAddr;
        this.requestUrl = requestUrl;
        this.data = data;
        this.callback = callback;
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

//    private String receiverId;
//
//    private byte[] data;
//
//    private String requestUrl;


//    public OutCoAPMessage(byte[] data, InetSocketAddress receiverAddr) {
//        super();
//        super.setData(data);
//        super.setReceiverAddr(receiverAddr);
//    }
//
//    public OutCoAPMessage(byte[] data, String requestUrl, InetSocketAddress receiverAddr) {
//        super();
//        super.setData(data);
//        super.setRequestUrl(requestUrl);
//        super.setReceiverAddr(receiverAddr);
//    }

//    public InetSocketAddress getReceiverAddr() {
//        return super.getReceiverAddr();
//    }



}
