package com.cislc.shadow.utils.enums;

/**
 * @Author: bin
 * @Date: 2019/10/31 14:54
 * @Description:
 */
public enum Protocol {

    // 通信使用的协议
    COAP(0, "coap"),
    DTLS(1, "dtls"),
    MQTT(2, "mqtt"),
    HTTP(3, "http");

    public int value;
    public String name;

    Protocol(int value, String name) {
        this.value = value;
        this.name = name;
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

    public static Protocol getByName(final String name) {
        for (Protocol protocol : Protocol.values()) {
            if (protocol.name.equals(name)) {
                return protocol;
            }
        }
        throw new IllegalArgumentException("Unknown Protocol type: " + name);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
