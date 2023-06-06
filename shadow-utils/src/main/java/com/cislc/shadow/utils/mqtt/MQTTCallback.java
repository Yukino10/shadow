package com.cislc.shadow.utils.mqtt;

public interface MQTTCallback {
    //收到订阅的MQTT消息
    void MQTTmessageArrived(String topic,String message);
    //MQTT消息发布成功
    void deliveryComplete(boolean isComplete);
    //MQTT连接丢失
    void connectionLost(Throwable throwable);
}
