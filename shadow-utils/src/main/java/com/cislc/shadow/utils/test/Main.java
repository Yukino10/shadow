package com.cislc.shadow.utils.test;

import com.cislc.shadow.utils.mqtt.MQTTCallback;
import com.cislc.shadow.utils.ShadowClient;
import com.cislc.shadow.utils.coap.CoAPServerCallback;

public class Main {
    public static void main(String[] args) {
        String[] topics = {"server11","testjar_topic"};
        int[] qos = {0,0};
        ShadowClient shadowClient = new ShadowClient("shadow_utils");
        shadowClient.startCoAP(new CoAPServerCallback() {
            @Override
            public String callback(String inMessage) {
                System.out.println("CoAP收到消息-->"+inMessage);
                // coap 服务允许程序以 response的形式返回结果， 如果没有返回值，可以返回null
                return null;
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        shadowClient.sendMessage("localhost","我是测试CoAP");
//        shadowClient.syncTime(new TimeCallback() {
//            @Override
//            public void syncComplete(String time) {
//                System.out.println("同步时间-->"+time);
//            }
//        });
        shadowClient.connectMQTT(new MQTTCallback() {
            @Override
            public void MQTTmessageArrived(String topic, String message) {
                System.out.println("topic-->"+topic);
                System.out.println("message-->"+message);
            }

            @Override
            public void deliveryComplete(boolean isComplete) {
                System.out.println("isComplete-->"+isComplete);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("throwable-->"+throwable);
            }

        });
        shadowClient.subscribe(topics,qos);
        for (int i=0;i<10;i++){
            String msg = "我是shadow_utils-->"+i;
            shadowClient.publishMQTTmsg(0,false,"shadow_utils_topic",msg);
        }
//        shadowClient.publishMQTTmsg(0,false,"shadow_utils_topic","我是shadow_utils");
//        shadowClient.startHeartbeats(new HeartBeatsCallback() {
//            @Override
//            public void heartActive() {
//                System.out.println();
//            }
//
//            @Override
//            public void heartInactive() {
//
//            }
//
//            @Override
//            public void heartException(Throwable cause) {
//
//            }
//        });
    }

}
