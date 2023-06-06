package com.cislc.shadow.utils;

import com.cislc.shadow.utils.coap.CoAPClient;
import com.cislc.shadow.utils.coap.CoAPServerCallback;
import com.cislc.shadow.utils.heartbeats.HeartBeatsCallback;
import com.cislc.shadow.utils.mqtt.MQTTCallback;
import com.cislc.shadow.utils.mqtt.MQTTClient;
import com.cislc.shadow.utils.syncTime.SyncTime;
import com.cislc.shadow.utils.syncTime.TimeCallback;
import com.cislc.shadow.utils.heartbeats.HeartBeatClient;
import com.cislc.shadow.utils.objcetStorage.OssServer;

import java.util.concurrent.*;

public class ShadowClient {
    public String clientid;
    public static ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue(6);
    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2,6,50, TimeUnit.MILLISECONDS,blockingQueue);
    public ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
       testOssDownload();
    }

    public static void testOssUpload(){
        String localPath = "E:\\Picture\\test.jpg";
        String objectName = OssServer.upload("test",localPath);
        System.out.println(objectName);
    }

    public static void testOssDownload(){
        String path = OssServer.download("test","E:\\Picture\\test_download.jpg");
        if (path != null){
            System.out.println("下载成功");
        }else {
            System.out.println("下载失败");
        }
    }
    public ShadowClient( String clientid) {
        this.clientid = clientid;
    }

    /**
     * 同步时间
     */
    public void syncTime(TimeCallback timeCallback){
        SyncTime syncTime = new SyncTime(timeCallback);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                syncTime.getDateTime();
            }
        };
        poolExecutor.execute(runnable);
    }

    /**
     * 连接MQTT服务器
     * @param clientid
     */
    MQTTClient mqttClient;
    public void connectMQTT(MQTTCallback mqttCallback){
        mqttClient = new MQTTClient(clientid,mqttCallback);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mqttClient.connect();
            }
        };
        singleThreadExecutor.execute(runnable);
    }

    /**
     * 订阅MQTT消息
     * @param topic
     * @param qos
     */
    public void subscribe(String[] topic, int[] qos){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mqttClient.subscribe(topic,qos);
            }
        };
        singleThreadExecutor.execute(runnable);
    }

    /**
     * 发布MQTT消息
     * @param qos--指定发送订阅数据方式:0-最多一次；1-至少一次；2-只有一次
     * @param retained 是否保留消息. Broker会存储每个Topic的最后一条保留消息及其Qos，当订阅该Topic的客户端上线后，Broker需要将该消息投递给它。
     *      *          保留消息作用：可以让新订阅的客户端得到发布方的最新的状态值，而不必要等待发送。
     * @param topic 消息主题
     * @param message 消息体
     */
    public void publishMQTTmsg(int qos, boolean retained, String topic, String message){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mqttClient.publish(qos,retained,topic,message);
            }
        };
        singleThreadExecutor.execute(runnable);
    }

    /**
     * 断开与MQTT服务器的连接
     */
    public void closeConnect(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mqttClient.closeConnect();
            }
        };
        singleThreadExecutor.execute(runnable);
    }

    /**
     * 开始心跳
     */
    public void startHeartbeats(HeartBeatsCallback heartBeatsCallback){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HeartBeatClient heartBeatClient = new HeartBeatClient(clientid,heartBeatsCallback);
                heartBeatClient.start();
            }
        };
        poolExecutor.execute(runnable);
    }

    /**
     * 启动sever接收消悉
     * @param callback server收到消息时的回调
     */
    public void startCoAP(CoAPServerCallback callback){
        CoAPClient.startServer(callback);
    }

    public void sendMessage(String ip, String payload){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = payload.getBytes();
                CoAPClient.sendMessage(ip,bytes);
            }
        };
        poolExecutor.execute(runnable);
    }
}
