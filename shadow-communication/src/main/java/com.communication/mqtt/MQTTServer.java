package com.communication.mqtt;

import com.communication.common.*;
import com.communication.utils.CommonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.eclipse.californium.elements.util.DaemonThreadFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: bin
 * @Date: 2019/11/1 8:47
 * @Description:
 */
public class MQTTServer {

    static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new DaemonThreadFactory("MQTTServer#"));

    /**
     * 开一个线程启动server
     * @param queue
     */
    public static void startAServer(String queue) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    startServer(queue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 启动Server
     * 负责接收消息
     * 判断是否是回调
     * 1、进入相应端口 2、执行回调
     */
    public static void startServer(String queue) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(CommonUtil.MQTT_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queue, false, false, false, null);
        System.out.println(" [*] Waiting for messages in queue: " + queue + " . To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(Thread.currentThread().getName() + " consumerTag: " + consumerTag + " [x] Received '" + message + "'");
            /** 将收到的byte[] 解析为 {@link MQTTSendMessage} 判断是否进行回调，进而解析为 {@link InMQTTMessage} */
            MQTTSendMessage sendMessage = MessageTransUtil.parseInMQTTBytes(delivery.getBody());
//            System.out.println(sendMessage.toString());
            InMQTTMessage inMQTTMessage = new InMQTTMessage(
                    new DeviceAddr(CommonUtil.MQTT_HOST, sendMessage.senderQueue, sendMessage.senderId),
                    sendMessage.reqUrl, sendMessage.data, sendMessage.uuid);
            InMessage inMessage = MessageTransUtil.transMQTTToInMessage(inMQTTMessage);
            System.out.println("mqtt server receive a message -> " + new String(inMessage.getData()));
            if (sendMessage.isResp()) {
                /** 如果是回调 解析为 {@link com.communication.common.InMessage} 并执行回调  */
                // 这里不实现 OutMessage返回值的接口，用户完全可以自行再次执行回调。
                MQTTCallBackMap.getCallBack(inMQTTMessage.getUuid()).execute(inMessage);
            } else {
                /** 如果不是回调 同样解析后 进入接口 执行接口中内容。*/
                /** TODO 这里要考虑是否存在返回值的情况，如果存在返回值，则应再次以同样的uuid发送消息*/
//                System.out.println("inside interface ..");
                MethodInfo method = MethodMap.getMethod(sendMessage.reqUrl);
                try {
//                    System.out.println("返回值类型" + method.getMethod().getReturnType().getName());
                    if (method.getMethod().getReturnType().getName().equals("void")) {
                        // 执行无返回值的接口
//                        System.out.println("interface with void return");
                        method.getMethod().invoke(method.getObject(), inMessage);
                    } else {
//                        System.out.println("interface with outmessage return");
                        // 执行有返回值的接口
                        OutMessage outMessage = (OutMessage) method.getMethod().invoke(method.getObject(), inMessage);
//                        System.out.println("outMessage is : " + outMessage.toString());
                        // 格式转换
                        OutMQTTMessage outMQTTMessage = MessageTransUtil.transOutMessageToMQTT(outMessage);
                        // 增加之前的uuid
                        outMQTTMessage.setUuid(sendMessage.uuid);
                        // 封装准备发送的byte[]
                        MQTTSendMessage mqttSendMessage = MessageTransUtil.transOutMQTTMessageToMQTTSendMessage(outMQTTMessage, true);
                        // 重置 byte[]
                        outMQTTMessage.setData(MessageTransUtil.getSendData(mqttSendMessage));
                        // 启用新线程发送响应。
                        MQTTClientPool.trySendMessage(outMQTTMessage);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                // 执行方法，并考虑返回值的状态，决定是否发送返回结果
            }
            /** 以上内容执行完成后，若有返回信息 其uuid与输入相同 */

        };
        channel.basicConsume(queue, true, deliverCallback, consumerTag -> { });

    }


}
