package com.cislc.shadow.utils.mqtts;

import com.cislc.shadow.utils.thread.MqttThreadPool;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttFactory {

    private static final Logger log = LoggerFactory.getLogger(MqttFactory.class);

    private static MqttClient mqttClient;
    private static MqttCallback pushCallback;
    static MQTTServerCallback callback;
    private static MemoryPersistence memoryPersistence;

    // mqtt配置
    private static String PREFIX;
    private static String HOST;
    private static String PORT;
    private static String USERNAME;
    private static String PASSWORD;
    private static String CLIENT_ID;
    private static int TIMEOUT;
    private static int KEEP_ALIVE;

    private static void createMqttClient() {
        try {
            memoryPersistence = new MemoryPersistence();
            String url = PREFIX + HOST;
            mqttClient = new MqttClient(url, CLIENT_ID, memoryPersistence);
            if (pushCallback == null){
                pushCallback = new PushCallback();
            }
            connect();
        } catch (MqttException e) {
            log.error("create mqtt client failed", e);
        }
    }

    /**
     * @return boolean
     * @author wangzhaosen@runhangtech.com
     * @Date 2019/1/18 0018 20:31
     * @Description 校验是否连接。
     **/
    public static synchronized Boolean isConnect() {
        if (mqttClient == null) {
            return Boolean.FALSE;
        }
        return mqttClient.isConnected();
    }

    /**
     * 连接mqtt服务器
     */
    public static void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            // 设置是否清空session，这里如果设置为false表示服务器会保留客户端的连接记录，
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(USERNAME);
            //设置连接密码
            options.setPassword(PASSWORD.toCharArray());
            //设置超时时间
            options.setConnectionTimeout(TIMEOUT);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(KEEP_ALIVE);
            //设置回调
            mqttClient.setCallback(pushCallback);
            mqttClient.connect(options);
        } catch (MqttException e) {
            log.error("mqtt connect failed", e);
        }
    }

    /**
     * 发布接口
     *
     * @param qos：订阅发送数据方式。
     * @param retained：是否保留消息-1：保留；0：不保留。 Broker会存储每个Topic的最后一条保留消息及其Qos，当订阅该Topic的客户端上线后，Broker需要将该消息投递给它。
     *                                    保留消息作用：可以让新订阅的客户端得到发布方的最新的状态值，而不必要等待发送。
     * @param topic：发布消息主题。
     * @param pushMessage：发布消息内容。
     */
    public static void publish(int qos, boolean retained, String topic, String pushMessage) {
        log.info("publish message, topic: {}, msg: {}", topic, pushMessage);

        /* 1.client是否存在，不存在的话，新建一个*/
        if (mqttClient == null) {
            createMqttClient();
        }

        /* 2.client是否已经连接中，没有连接则重新进行连接*/
        if (!mqttClient.isConnected()) {
            connect();
        }

        /* 3.准备推送数据*/
        MqttThreadPool.getThreadPool().addTask(() -> {
            MqttMessage message = new MqttMessage();
            message.setQos(qos);
            message.setRetained(retained);
            message.setPayload(pushMessage.getBytes());
            MqttTopic mTopic = mqttClient.getTopic(topic);
            if (null == mTopic) {
                subscribe(topic, qos);
                mTopic = mqttClient.getTopic(topic);
            }
            /*4.推送数据*/
            MqttDeliveryToken token;
            try {
                token = mTopic.publish(message);
                token.waitForCompletion();
            } catch (Exception e) {
                log.error("MQTT发布数据异常，主题： {}，内容： {}，出错信息： {}", topic, message, e.getMessage());
            }
        });
    }

    /**
     * @author wangzhaosen@runhangtech.com
     * @Date 2019/1/18 16:33
     * @Description 订阅某个主题。
     **/
    public static void subscribe(String topic, int qos) {
        log.info("subscribe topic: {}", topic);

        /* 1.不存在client，则新建一个client*/
        if (mqttClient == null) {
            createMqttClient();
        }

        /* 2.未连接上，则建立一个连接*/
        if (!mqttClient.isConnected()) {
            connect();
        }

        /* 3.订阅主题*/
        subscribe(mqttClient, topic, qos);
    }

    /**
     * @author wangzhaosen@runhangtech.com
     * @Date 2019/1/18 16:27
     * @Description 订阅主题。
     **/
    public static void subscribe(MqttClient mqttClient, String topic, int qos) {
        try {
            if (mqttClient != null) {
                mqttClient.subscribe(topic, qos);
            }
        } catch (MqttException e) {
            log.error("subscribe: 订阅主题报错，主题： {}, {}", topic, e.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnect(){
        //关闭存储方式
        if (null != memoryPersistence) {
            try {
                memoryPersistence.close();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            }
        } else {
            log.warn("closeConnect: memoryPersistence is null");
        }
        //关闭连接
        if (null != mqttClient) {
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                } catch (MqttException e) {
                    log.error("mqtt close failed", e);
                }
            } else {
                log.warn("closeConnect: mqttClient is not connect");
            }
        }
        else {
            log.warn("closeConnect: mqttClient is null" );
        }
    }

    public static void setCallback(MQTTServerCallback callback) {
        MqttFactory.callback = callback;
    }

    @Value("${shadow.mqtt.username:guest}")
    public void setUsername(String username) {
        MqttFactory.USERNAME = username;
    }

    @Value("${shadow.mqtt.password:guest}")
    public void setPassword(String password) {
        MqttFactory.PASSWORD = password;
    }

    @Value("${shadow.mqtt.timeout:60}")
    public void setTimeout(int timeout) {
        MqttFactory.TIMEOUT = timeout;
    }

    @Value("${shadow.mqtt.keepAlive:180}")
    public void setKeepAlive(int keepAlive) {
        MqttFactory.KEEP_ALIVE = keepAlive;
    }

    @Value("${shadow.mqtt.clientId:temp}")
    public void setClientId(String clientId) {
        MqttFactory.CLIENT_ID = clientId;
    }

    @Value("${shadow.mqtt.host:127.0.0.1}")
    public void setHost(String HOST) {
        MqttFactory.HOST = HOST;
    }

    @Value("${shadow.mqtt.port:1883}")
    public void setPort(String PORT) {
        MqttFactory.PORT = PORT;
    }

    @Value("${shadow.mqtt.preFix:'tcp://'}")
    public void setPrefix(String PREFIX) {
        MqttFactory.PREFIX = PREFIX;
    }
}
