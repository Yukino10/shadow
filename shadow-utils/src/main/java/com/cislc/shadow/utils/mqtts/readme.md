#MQTT 使用方法
## 配置MQTT
```java
public class configDemo {
    public void configMethod() {
        // 配置mqtt服务器
        MqttFactory.setParam(url, username, password, clientId, timeout, keepAlive);
    }
}
```
## 发送消息
```java
public class sendMessageDemo {
    public void testFunction() {
        // 接收方MQTT的订阅主题
        String topic = "test_topic";
        // 向接收方发送的消息体
        String message = "test message";
        // 发送MQTT消息
        MQTTClient.publish(qos, retained, topic, pushMessage); 
    }   
}
```

## 订阅主题
```code
// 为Server指定一个topic， qos为服务质量
MQTTClient.subscribe(topic, qos);   
```

## 收到消息时的回调
```code
MqttFactory.setCallback((String inMessage) -> {
    // 打印收到的消息
    System.out.println(inMessage);
});
```
