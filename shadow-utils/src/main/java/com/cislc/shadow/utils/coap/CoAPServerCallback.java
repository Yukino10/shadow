package com.cislc.shadow.utils.coap;

/**
 * @Author: bin
 * @Date: 2019/11/19 19:25
 * @Description:
 */
public interface CoAPServerCallback {
    /**
     * 作为coap Server的回调函数
     * 返回值将被当作响应，返回至请求方。
     * @param inMessage
     * @return
     */
    String callback(String inMessage);
}
