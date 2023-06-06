package com.communication.mqtt;

import com.communication.common.ShadowCallBack;
import com.communication.utils.CommonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.eclipse.californium.elements.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @Author: bin
 * @Date: 2019/11/4 15:41
 * @Description:
 */
public class MQTTClientPool {
    static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new DaemonThreadFactory("MQTTClient#"));

    static Connection connection = createConnection();

    private static Connection createConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(CommonUtil.MQTT_USERNAME);
        factory.setPassword(CommonUtil.MQTT_PASSWORD);
        factory.setPort(CommonUtil.MQTT_PORT);
        factory.setHost(CommonUtil.getCurrentDeviceAddr().getMqttAddress().getIp());
        try {
            return factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 使用反射调用client 发送list中的消息
     */
    @Deprecated
    public static void trySendMessage() {

    }




    /**
     * 发送消息，并执行回调
     * @param outMQTTMessage
     * outMQTTMessage中 data为 已处理好的data，应直接发送。
     */
    public static void trySendMessage(OutMQTTMessage outMQTTMessage) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (outMQTTMessage != null) {
                    try {
                        Channel channel = connection.createChannel();
                        channel.queueDeclare(CommonUtil.getCurrentDeviceAddr().getMqttAddress().getQueueName(), false, false, false, null);
                        channel.basicPublish("", outMQTTMessage.getReceiverAddr().getMqttAddress().getQueueName(), null, outMQTTMessage.getData());
//                        System.out.println(" [x] Sent '" + new String(outMQTTMessage.getData()) + "'");
                        /** 判断 {@link OutMQTTMessage} 的 {@link ShadowCallBack} 是否 为空*/
                        if (outMQTTMessage.getCallback() != null) {
                            /** 如果不为空 需要在发送完消息后将 {@link outMQTTMessage} 存入待回调的 Map*/
                            MQTTCallBackMap.putCallBack(outMQTTMessage.getUuid(), outMQTTMessage.getCallback());
                        }
                        /** 如果为空， 执行正常发送流程 其是否为响应 状态为真*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}
