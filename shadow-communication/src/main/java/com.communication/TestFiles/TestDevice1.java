package com.communication.TestFiles;

import com.communication.coap.CoAPServer;
import com.communication.common.CommunicationScanner;
import com.communication.mqtt.MQTTServer;
import com.communication.utils.CommonUtil;

/**
 * @Author: bin
 * @Date: 2019/11/12 13:42
 * @Description:
 */
public class TestDevice1 {

    public static void main(String[] args) {

        // 全局扫包
        CommunicationScanner.scan();
        // 启动coap server
        CoAPServer.start();
        // 启动 mqtt server
        MQTTServer.startAServer(CommonUtil.WIN7_QUEUE);

    }


}
