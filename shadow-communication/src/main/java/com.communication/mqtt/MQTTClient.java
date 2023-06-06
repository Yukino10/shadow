package com.communication.mqtt;

import com.rabbitmq.client.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: bin
 * @Date: 2019/11/1 8:46
 * @Description:
 */
public class MQTTClient {
    private final static String QUEUE_NAME = "hello";
    private final static String QUEUE_NAME_BACK = "hello_rep";

    public static void main(String[] args) {


        test2();
    }

    /**
     * 普通client发，server收。
     */
    public static void test1() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("120.78.133.4");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * client 发，server收并回复，client接收回复。
     */
    public static void test2() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("120.78.133.4");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            /** send */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(Thread.currentThread().getName() + " [x] Sent '" + message + "'");
            /** receive */
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost("120.78.133.4");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME_BACK, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            /** 这里是用户定义的逻辑，我只是执行用户的回调而已。*/
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            delivery.getEnvelope().
                String messageBack = new String(delivery.getBody(), "UTF-8");
//                messageBackInMain = messageBack;
                System.out.println(Thread.currentThread().getName() + " consumerTag: " + consumerTag + " [x] Received '" + messageBack + "'");
            };
            channel.basicConsume(QUEUE_NAME_BACK, true, deliverCallback, consumerTag -> { });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
