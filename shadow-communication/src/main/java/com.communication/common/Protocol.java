package com.communication.common;

import org.eclipse.californium.core.coap.CoAP;

/**
 * @Author: bin
 * @Date: 2019/10/31 14:54
 * @Description:
 */
public enum Protocol {

    // 通信使用的协议
    COAP(0),
    DTLS(1),
    MQTT(2),
    HTTP(3);
    public int value;
    Protocol(int value) {
        this.value = value;
    }
    public static Protocol valueOf(final int value) {
        switch (value) {
            case 0: return COAP;
            case 1: return DTLS;
            case 2: return MQTT;
            case 3: return HTTP;
            default: throw new IllegalArgumentException("Unknown Protocol type " + value);
        }
    }
}
