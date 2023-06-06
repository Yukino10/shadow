#coap 使用方法
##Maven依赖
```xml
<dependencies>
    <dependency>
        <groupId>org.eclipse.californium</groupId>
        <artifactId>californium-core</artifactId>
        <version>2.0.0-M16</version>
    </dependency>
    <dependency>
        <groupId>org.eclipse.californium</groupId>
        <artifactId>element-connector</artifactId>
        <version>2.0.0-M16</version>
    </dependency>
    <dependency>
        <groupId>org.eclipse.californium</groupId>
        <artifactId>scandium</artifactId>
        <version>2.0.0-M16</version>
    </dependency>
</dependencies>
```
##配置文件
正常情况下，在项目初次启动时，Californium会在项目根目录创建名为"Californium.properties"的配置文件，如果没有自动创建，用户可自行创建，并进行如下配置即可。  
注：  
1、以下配置的第一行注释请务必保留。  
2、用户可调整以下配置中添加注释的配置，其他配置原样保留即可。
```yaml
#Californium CoAP Properties file
#Thu Dec 12 11:08:20 CST 2019
HTTP_SERVER_SOCKET_BUFFER_SIZE=8192
UDP_CONNECTOR_OUT_CAPACITY=2147483647
#拥塞控制算法
CONGESTION_CONTROL_ALGORITHM=Cocoa
#使用拥塞控制
USE_CONGESTION_CONTROL=false
#超时确认时间 (单位:ms) 发送消息后，经过此长度的时间，若没有收到确认，则使用重传机制
ACK_TIMEOUT=2000
MAX_ACTIVE_PEERS=150000
TCP_CONNECT_TIMEOUT=10000
NOTIFICATION_CHECK_INTERVAL_COUNT=100
MAX_MESSAGE_SIZE=1024
MID_TRACKER_GROUPS=16
DEDUPLICATOR=DEDUPLICATOR_MARK_AND_SWEEP
TCP_CONNECTION_IDLE_TIMEOUT=10
#coap的默认端口
COAP_PORT=5683
ACK_TIMEOUT_SCALE=2.0
PREFERRED_BLOCK_SIZE=512
NETWORK_STAGE_RECEIVER_THREAD_COUNT=8
DTLS_AUTO_RESUME_TIMEOUT=30000
MAX_LATENCY=100000
MAX_SERVER_RESPONSE_DELAY=250000
DTLS_CONNECTION_ID_LENGTH=
PROTOCOL_STAGE_THREAD_COUNT=8
#最大重传等待时间
MAX_TRANSMIT_WAIT=93000
MULTICAST_BASE_MID=65000
UDP_CONNECTOR_RECEIVE_BUFFER=0
EXCHANGE_LIFETIME=247000
HTTP_SERVER_SOCKET_TIMEOUT=100000
CROP_ROTATION_PERIOD=2000
UDP_CONNECTOR_DATAGRAM_SIZE=2048
##最大尝试重传次数
MAX_RETRANSMIT=4
MAX_PEER_INACTIVITY_PERIOD=600
MAX_RESOURCE_BODY_SIZE=8192
NOTIFICATION_CHECK_INTERVAL=86400000
LEISURE=5000
HTTP_CACHE_RESPONSE_MAX_AGE=86400
BLOCKWISE_STATUS_LIFETIME=300000
RESPONSE_MATCHING=STRICT
UDP_CONNECTOR_SEND_BUFFER=0
MID_TACKER=GROUPED
TCP_WORKER_THREADS=1
NETWORK_STAGE_SENDER_THREAD_COUNT=8
NON_LIFETIME=145000
TOKEN_SIZE_LIMIT=8
HTTP_PORT=8080
MARK_AND_SWEEP_INTERVAL=10000
HEALTH_STATUS_INTERVAL=0
ACK_RANDOM_FACTOR=1.5
SECURE_SESSION_TIMEOUT=86400
NSTART=1
USE_RANDOM_MID_START=true
HTTP_CACHE_SIZE=32
TLS_HANDSHAKE_TIMEOUT=10000
DTLS_CONNECTION_ID_NODE_ID=
PROBING_RATE=1.0
BLOCKWISE_STRICT_BLOCK2_OPTION=false
NOTIFICATION_REREGISTRATION_BACKOFF=2000
COAP_SECURE_PORT=5684
```
##设置消息发送失败时的处理方法
```java
public class ErrorHandlerDemo {
    public static void errorHandlerMethod() {
        // 设置消息发送失败时的回调。如果经过一定次数的重传后仍失败，则执行以下回调。
        CoAPClient.setMessageExceptionHandler((ip, message) -> {
            System.out.println("发送至 " + ip + " 的消息： " + message + " 没有收到。");
        });
    }
}
```
如果未设置以上回调函数在发送消息时将打印如下警告  
"you have not set message exception handler."

##发送消息
```java
public class SendMessageDemo {
    public static void sendMethod() {
        // 向 本机(127.0.0.1)发送一条消息， 消息体为 "this is a test message"
        CoAPClient.sendMessage("127.0.0.1", "this is a test message");
    }
}
```

##接收消息(启动CoAPServer)
```java
public class ReceiveMessageDemo {
    public static void receiveMethod() {
        // 启动 Server，当收到消息时，执行兰布达表达式中方法
        CoAPClient.startServer((String inMessage) -> {
            // 当server收到消息时的逻辑...
            System.out.println("receive a message --> " + inMessage);
            return null;
        });
    }
}
```

##异常信息


异常  
信息
