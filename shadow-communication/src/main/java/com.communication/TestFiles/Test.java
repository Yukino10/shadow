package com.communication.TestFiles;

import com.communication.coap.CoAPServer;
import com.communication.common.DeviceAddr;
import com.communication.common.InMessage;
import com.communication.common.OutMessage;
import com.communication.common.Protocol;
import com.communication.mqtt.MQTTServer;
import com.communication.utils.CommonUtil;

/**
 * @Author: bin
 * @Date: 2019/11/7 17:23
 * @Description:
 */
public class Test {
    public static final String RECEIVE_DEVICE_ID = "win7";
    public static final String WIN10_QUEUE = "win10_queue";
    public static final String WIN7_QUEUE = "win7_queue";



    public static void main(String[] args) {
//        coAPTest();
        System.out.println("------divider-------");
        mqttTest();
    }

    public static void coAPTest() {
        CoAPServer.start();

        // 根据设备id找到coap设备的地址
        DeviceAddr receiverAddr = CommonUtil.getDeviceAddrByDeviceId(RECEIVE_DEVICE_ID);
        // 向 coap 地址发送消息
//        new OutMessage(deviceAddr, "this is a test coap data".getBytes(), "hello").send();
        new OutMessage(Protocol.COAP, receiverAddr, "this is a test coap data".getBytes(), "hello").send();


        // coap 回调
        new OutMessage(Protocol.COAP, receiverAddr, "this is a test coap data with call back".getBytes(),
                "sys_hello_with_back", (InMessage inMessage) -> {
            System.out.println("this is a callback message with coap ----> " + new String(inMessage.getData()));
        }).send();

    }

    static void mqttTest() {

        try {

            MQTTServer.startAServer(WIN10_QUEUE);
            System.out.println("mqtt server started. ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        DeviceAddr receiverAddr = CommonUtil.getDeviceAddrByDeviceId(RECEIVE_DEVICE_ID);
        new OutMessage(Protocol.MQTT, receiverAddr, "this is a test mqtt data".getBytes(), "hello").send();

        /** 发送后执行回调 */
        // mqtt 回调
        new OutMessage(Protocol.MQTT, receiverAddr, "this is a test mqtt data with call back".getBytes(),
                "sys_hello_with_back", (InMessage inMessage) -> {
            System.out.println("this is a callback message with mqtt ----> " + new String(inMessage.getData()));
        }).send();

        /** 发送后执行回调，在回调中请求其他内容，再回调*/
        new OutMessage(Protocol.MQTT, receiverAddr, "this is a test mqtt data with call back".getBytes(),
                "sys_hello_with_back", (InMessage inMessage) -> {
            System.out.println("this is a callback message with mqtt 1----> " + new String(inMessage.getData()));
            new OutMessage(Protocol.MQTT, receiverAddr, "this is a test mqtt data with call back".getBytes(),
                    "sys_hello_with_back", (InMessage inMessage2) -> {
                System.out.println("this is a callback message with mqtt 2----> " + new String(inMessage2.getData()));
            }).send();
        }).send();
    }
}
