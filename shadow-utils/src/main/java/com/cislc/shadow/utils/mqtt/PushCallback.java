package com.cislc.shadow.utils.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PushCallback implements MqttCallback {
    MQTTCallback mqttCallback;

    public PushCallback(MQTTCallback mqttCallback) {
        this.mqttCallback = mqttCallback;
    }

    public void connectionLost(Throwable throwable) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连");
        mqttCallback.connectionLost(throwable);
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息Qos : " + mqttMessage.getQos());
//        System.out.println("接收消息内容 : " + new String(mqttMessage.getPayload()));
        String message = new String(mqttMessage.getPayload());
        mqttCallback.MQTTmessageArrived(s,message);
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // subscribe后得到的消息会执行到这里面
//        System.out.println("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
        mqttCallback.deliveryComplete(iMqttDeliveryToken.isComplete());
    }
}
