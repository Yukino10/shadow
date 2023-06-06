package com.communication.utils;

import com.communication.common.DeviceAddr;
import com.communication.common.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: bin
 * @Date: 2019/11/11 15:58
 * @Description:
 */
public class CommonUtil {

    /**
     * 必须获取的参数
     */
    public static final String CURRENT_DEVICE_ID = "win10";


    public static final String WIN10_IP = "192.168.1.118";
    public static final String WIN7_IP = "211.87.235.74";
    public static final String WIN10_QUEUE = "win10_queue";
    public static final String WIN7_QUEUE = "win7_queue";

    /** 常量 */
    public static final String DEVICE_TYPE_SERVER = "SERVER"; // 如果不是server 还应配置server的通讯信息，公网ip、uuid。
    public static final String DEVICE_TYPE_CLIENT = "CLIENT"; // 如果不是server 还应配置server的通讯信息，公网ip、uuid。

    /** TODO 从yml配置文件中读取的内容 */
    public static final String DEVICE_TYPE = "SERVER"; // 如果不是server 还应配置server的通讯信息，公网ip、uuid。
    // 配置的可用的通讯方式，如果没有指定默认通讯方式，将以第一个为默认
    public static final String PROTOCOL_LIST_STR = "[\"MQTT\", \"COAP\"]";
    public static final List<Protocol> PROTOCOL_LIST = initProtocolList();
    public static final String DEFAULT_PROTOCOL_STR = null;
    // 默认使用的通讯地址
    public static final Protocol DEFAULT_PROTOCOL = initDefaultProtocol();


    /**
     * 将形如[\"MQTT\", \"COAP\"]的字符串解析为协议链表
     * @return
     */
    public static List<Protocol> initProtocolList() {
        List<Protocol> result = new ArrayList<>();
        String[] protocols = PROTOCOL_LIST_STR.split(",");
        for (String str : protocols) {
            str = str.replace("[", "");
            str = str.replace("]", "");
            str = str.replaceAll(" ", "");
            str = str.replaceAll("\"", "");
            result.add(Protocol.valueOf(str));
        }
        return result;
    }

    /**
     * 读入配置文件后，设置默认通信协议
     * @return
     */
    public static Protocol initDefaultProtocol() {
        if (DEFAULT_PROTOCOL_STR == null) {
            if (PROTOCOL_LIST.size() == 0) {
                System.out.println("没有配置可用的通信方式");
//                throw Exception
                return null;
            } else {
                return PROTOCOL_LIST.get(0);
            }
        } else {
            return Protocol.valueOf(DEFAULT_PROTOCOL_STR);
        }
    }
    // 需要被扫描的包
    public static final String SCAN_PACKAGE = null;
    // uuid的存放路径
    public static final String DEVICE_ID_LOCATION = "./test";
    /** COAP Util */
    // coap 的端口
    public static final int COAP_PORT = 5684;
    /** MQTT Util */
    // mqtt 服务器的账号密码
    public static final String MQTT_HOST = "120.78.133.4";
    public static final String MQTT_USERNAME = "guest";
    public static final String MQTT_PASSWORD = "guest";
    public static final int MQTT_PORT = 15672;


    public static String createUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    /**
     * 外部类应包含此方法。 根据设备id找到设备通信地址
     * @param deviceId
     * @return
     */
    public static DeviceAddr getDeviceAddrByDeviceId(String deviceId) {
        switch (deviceId) {
            case "win10":
                DeviceAddr deviceAddr = new DeviceAddr(WIN10_IP, COAP_PORT);
                deviceAddr.addMQTT(MQTT_HOST, WIN10_QUEUE);
                deviceAddr.setDeviceId(deviceId);
                return deviceAddr;
            case "win7":
                DeviceAddr deviceAddr1 = new DeviceAddr(WIN7_IP, COAP_PORT);
                deviceAddr1.addMQTT(MQTT_HOST, WIN7_QUEUE);
                deviceAddr1.setDeviceId(deviceId);
                return deviceAddr1;
            default:
                return new DeviceAddr("120.78.133.4", "testQueue2");
        }
    }

    /**
     * 外部类应包含此方法。 找到本机的通讯地址，或只是deviceId
     * @return
     */
    public static DeviceAddr getCurrentDeviceAddr() {
//        DeviceAddr result = new DeviceAddr();
//        return result;
        return getDeviceAddrByDeviceId(CURRENT_DEVICE_ID);
    }

}
