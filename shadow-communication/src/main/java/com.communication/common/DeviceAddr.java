package com.communication.common;



/**
 * @Author: bin
 * @Date: 2019/11/7 14:32
 * @Description:
 * 设备的通信地址信息，包含在影子设备中
 */
public class DeviceAddr {
    // 默认使用的协议。
    Protocol defaultProtocol;
    // coap地址，可为null
    CoAPAddress coAPAddress;
    // mqtt 地址， 可为null
    MQTTAddress mqttAddress;
    /** 设备的唯一标识 与 {@link ShadowModel} 中相同*/
    String deviceId;


    public class CoAPAddress {
        // coapServer的ip地址
        String ip;
        // coapServer的端口
        int port;

        CoAPAddress(String coAPIp, int port) {
            this.ip = coAPIp;
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }
    }

    public class MQTTAddress {
        // mqtt服务器的ip
        private String ip;
        // 此设备默认监听的接收消息的队列
        private String queueName;

        public MQTTAddress(String ip, String queueName) {
            this.ip = ip;
            this.queueName = queueName;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }
    }

    public MQTTAddress createMQTTAddress(String serverIp, String queueName) {
        return new MQTTAddress(serverIp, queueName);
    }
    public DeviceAddr() {

    }

    public DeviceAddr(MQTTAddress mqttAddress) {
        this.mqttAddress = mqttAddress;
        this.defaultProtocol = Protocol.MQTT;
    }

    public DeviceAddr(CoAPAddress coAPAddress) {
        this.coAPAddress = coAPAddress;
        this.defaultProtocol = Protocol.COAP;
    }

    /**
     * coap通信地址的构造方法
     * @param coAPIp
     * @param port
     */
    public DeviceAddr(String coAPIp, int port) {
        // 默认为coap协议
        this.defaultProtocol = Protocol.COAP;
        this.coAPAddress = new CoAPAddress(coAPIp, port);
    }
    public void addMQTT(String mqttIp, String queueName) {
        this.mqttAddress = new MQTTAddress(mqttIp, queueName);
    }

    /**
     * mqtt 通信地址的构造方法
     * @param mqttIp
     * @param queueName
     */
    public DeviceAddr(String mqttIp, String queueName) {
        // 默认为Mqtt协议
        this.defaultProtocol = Protocol.MQTT;
        this.mqttAddress = new MQTTAddress(mqttIp, queueName);
    }
    public DeviceAddr(String mqttIp, String queueName, String deviceId) {
        // 默认为Mqtt协议
        this.defaultProtocol = Protocol.MQTT;
        this.mqttAddress = new MQTTAddress(mqttIp, queueName);
        this.deviceId = deviceId;
    }
    public void addCoAP(String coAPIp, int port) {
        this.coAPAddress = new CoAPAddress(coAPIp, port);
    }

    public Protocol getDefaultProtocol() {
        return defaultProtocol;
    }

    public void setDefaultProtocol(Protocol defaultProtocol) {
        this.defaultProtocol = defaultProtocol;
    }

    public CoAPAddress getCoAPAddress() {
        return coAPAddress;
    }

    public void setCoAPAddress(CoAPAddress coAPAddress) {
        this.coAPAddress = coAPAddress;
    }

    public MQTTAddress getMqttAddress() {
        return mqttAddress;
    }

    public void setMqttAddress(MQTTAddress mqttAddress) {
        this.mqttAddress = mqttAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
