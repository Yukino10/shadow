package com.cislc.shadow.utils.coap;

/**
 * @Author: bin
 * @Date: 2019/12/31 10:20
 * @Description:
 */
public interface MessageExceptionHandler {
    // 当 californium 无法将消息送达目的服务器时，执行此回调。
    void onSendFailure(String ip, String message);
}
