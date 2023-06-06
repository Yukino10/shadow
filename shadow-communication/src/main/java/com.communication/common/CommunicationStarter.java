package com.communication.common;

/**
 * @Author: bin
 * @Date: 2019/11/19 10:58
 * @Description:
 */

/**
 * 启动时，
 * 1、检查指定位置是否有配置文件，（服务器默认有配置文件,）
 * 2.1、如果有读取配置文件（包含uuid的配置）
 * 2.1.1、根据配置文件（properties文件）中信息，判断启动哪些通讯服务。
 * 2.2、如果没有创建一个文件，生成一个uuid，此uuid作为mqtt的topic(如果有使用mqtt的话)和设备的通信id
 * 2.2.1、使用此uuid，组装出本机临时通信地址信息，并将此uuid发送至服务器进行注册。
 * 2.2.2、如果服务器拒绝使用此uuid，重复执行2.2
 * 2.2.3、服务器返回注册成功后将uuid存入文件中
 */

import com.communication.coap.CoAPServer;
import com.communication.coap.CoAPUtil;
import com.communication.mqtt.MQTTServer;
import com.communication.mqtt.MQTTUtil;
import com.communication.utils.CommonUtil;
import com.communication.utils.ConfigUtil;
import com.communication.utils.IpUtil;

import java.io.File;
import java.util.List;

/**
 * deviceType: server/device
 * uuid:
 * communicateWay: ["coap", "mqtt"]
 */
public class CommunicationStarter {
    public static void main(String[] args) {
        startService();
    }

//    public static void startService() {
//
//    }

    public static void startService() {

        // read yml / properties config

        // check communication uuid 检查uuid
        File file = new File(CommonUtil.DEVICE_ID_LOCATION);
        // 全局扫包
        CommunicationScanner.scan();

        String deviceId;
        if ((deviceId = ConfigUtil.tryReadDeviceIdFromFile(file)) == null) {
            // 未向服务器注册过通讯信息
            // 生成本机id uuid
            deviceId = CommonUtil.createUuid();
            // 1、读取 yml / properties 中配置的服务
            List<Protocol> protocolList = CommonUtil.PROTOCOL_LIST;
            // 启动配置列表中的服务
            startServicesWithProtocolList(protocolList, deviceId);
            // 初始化当前设备的通信地址
            ConfigUtil.currentDeviceAddr = generateDeviceAddr(deviceId, CommonUtil.DEFAULT_PROTOCOL, protocolList);

            // 请求注册
            if (!CommonUtil.DEVICE_TYPE.equals(CommonUtil.DEVICE_TYPE_SERVER)) {
                // 如果不是Server
                // TODO 所有服务启动后，向服务器发起注册 需要保证通讯服务已启动
                // 如果注册成功，保存uuid到指定位置
                // 如果不成功，重新生成uuid并注册

            } else {
                // 本机是Server 保存配置信息uuid等到指定位置
                ConfigUtil.writeDeviceIdToFile(deviceId, new File(CommonUtil.DEVICE_ID_LOCATION));
            }
            // 保存uuid

        } else {
            // 1、读取 yml / properties 中配置的服务
            List<Protocol> protocolList = CommonUtil.PROTOCOL_LIST;
            // 启动配置列表中的服务
            startServicesWithProtocolList(protocolList, deviceId);
        }
    }

    /**
     * 初始化当前设备的通信地址
     * @param deviceId
     * @param defaultProtocol
     * @param protocolList
     * @return
     */
    private static DeviceAddr generateDeviceAddr(String deviceId, Protocol defaultProtocol, List<Protocol> protocolList) {
        DeviceAddr deviceAddr = new DeviceAddr();
        deviceAddr.setDefaultProtocol(defaultProtocol);
        for (Protocol protocol : protocolList) {
            switch (protocol) {
                case COAP:
                    deviceAddr.addCoAP(IpUtil.INTERNET_IP, CommonUtil.COAP_PORT);
                    break;
                case MQTT:
                    deviceAddr.addMQTT(CommonUtil.MQTT_HOST, deviceId);
                    break;
                default:
            }
        }
        return deviceAddr;

    }
    private static void startServicesWithProtocolList(List<Protocol> protocolList, String deviceId) {

        for (Protocol protocol : protocolList) {
            switch (protocol) {
                case COAP:
                    // 如果本机存在外网ip
                    if (!CoAPUtil.checkCoAPConfig()) {
                        System.out.println("本机没有公网ip，无法使用coap服务");
//                            throw new Exception("本机没有公网ip，无法使用coap服务");
                        continue;
                    }
                    // 启动coap服务
                    CoAPServer.start();
                    // 组织本机coap的通讯地址信息

                    break;
                case MQTT:
                    if (!MQTTUtil.checkMQTTConfig()) {
                        System.out.println("mqtt 配置错误， ****");
                        continue;
                    }
                    // 启动mqtt服务
                    MQTTServer.startAServer(deviceId);

                    break;
                default:
            }
        }
    }

}
