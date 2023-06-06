package com.cislc.shadow.utils.mqtts;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushCallback implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(PushCallback.class);

    @Override
    public void connectionLost(Throwable cause) {
        // 断线重连
        log.warn("mqtt connect lost, reconnecting ...");
        MqttFactory.connect();
        if (MqttFactory.isConnect()) {
            log.info("mqtt connection retained.");
        } else {
            log.error("failed to reconnect mqtt server, cause: " + cause.getMessage());
        }
//        cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String msg = new String(message.getPayload());
        log.info("receive msg: {}", msg);
        MqttFactory.callback.callback(msg);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
//        callback.deliveryComplete(token.isComplete());
    }
}
