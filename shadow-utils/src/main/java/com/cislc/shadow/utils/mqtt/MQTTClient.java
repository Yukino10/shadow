package com.cislc.shadow.utils.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTClient {
//    public static final String HOST = "tcp://47.92.105.44:1883";
    public static final String HOST = "tcp://116.62.247.177:1883";
    private String userName = "guest";
    private String passWord = "guest";

    String clientid;
    MQTTCallback mqttCallback;
    private MqttClient client;
    private MqttConnectOptions options;
    MemoryPersistence memoryPersistence = new MemoryPersistence();
    public static final String TOPIC = "will";

    public MQTTClient(String clientid, MQTTCallback mqttCallback) {
        this.clientid = clientid;
        this.mqttCallback = mqttCallback;
    }

    public void connect(){
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, memoryPersistence);
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调
            client.setCallback(new PushCallback(mqttCallback));
//            MqttTopic topic = client.getTopic(TOPIC);
//            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//            options.setWill(topic, "close".getBytes(), 2, true);
            client.connect(options);
        } catch (Exception e) {
            System.out.println("连接MQTT服务器异常:"+e);
            e.printStackTrace();
        }
    }
    public void connect(String id) {
        clientid = id;
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, memoryPersistence);
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调
            client.setCallback(new PushCallback(mqttCallback));
//            MqttTopic topic = client.getTopic(TOPIC);
//            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
//            options.setWill(topic, "close".getBytes(), 2, true);
            client.connect(options);
        } catch (Exception e) {
            System.out.println("连接MQTT服务器异常:"+e);
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        //关闭存储方式
        if (null != memoryPersistence) {
            try {
                memoryPersistence.close();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("memoryPersistence is null");
        }
        //关闭连接
        if (null != client) {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                    client.close();
                } catch (MqttException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                System.out.println("mqttClient is not connect");
            }
        } else {
            System.out.println("mqttClient is null");
        }
    }

    public void publish(int qos, boolean retained, String topic, String payload) {
        if (client != null && client.isConnected()) {
            MqttMessage message = new MqttMessage();
            message.setQos(qos);
            message.setRetained(retained);
            message.setPayload(payload.getBytes());
            MqttTopic mqttTopic = client.getTopic(topic);
            if (mqttTopic != null) {
                try {
                    MqttDeliveryToken token = mqttTopic.publish(message);
                    if (token.isComplete()) {
                        System.out.println("消息发布成功");
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        } else {
            reConnect();
        }
    }

    //重新连接
    public void reConnect() {
        if (null != client) {
            if (!client.isConnected()) {
                if (null != options) {
                    try {
                        client.connect(options);
                    } catch (MqttException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("mqttConnectOptions is null");
                }
            } else {
                System.out.println("mqttClient is null or connect");
            }
        } else {
            connect(clientid);
        }
    }

    public void subscribe(String[] topics, int[] Qos) {
        try {
            if (client != null) {
                client.subscribe(topics, Qos);
            } else {
                System.out.println("MQTTClient-->subscribe-->client为空");
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
