package com.cislc.shadow.utils.coap;

/**
 * @Author: bin
 * @Date: 2019/11/19 19:25
 * @Description:
 */
public interface CoAPCallback {
    /**
     * 发送消息后的回调。
     * @param inMessage
     */
    void callback(String inMessage);
}
