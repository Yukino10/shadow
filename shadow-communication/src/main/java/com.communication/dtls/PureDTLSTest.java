package com.communication.dtls;

import com.communication.dtls.pureDtls.*;
import com.communication.coap.OutCoAPMessage;

import java.net.InetSocketAddress;

/**
 * @Author: bin
 * @Date: 2019/10/29 9:32
 * @Description:
 */
public class PureDTLSTest {

    public static void main(String[] args) {
        // 1、扫包
//        CommunicationScanner scanner = new CommunicationScanner();
//        scanner.addPackage("com.communication");
//        try {
//            scanner.getMapping();
//        } catch (Exception e) {
//            System.out.println("a fault happened when scanning package to find class marked with CommunicationController .");
//            e.printStackTrace();
//        }
//
//        // 2、启动Server
//        PureDtlsCoapServer server = new PureDtlsCoapServer();
//        server.startInThread();

        // 3、启动client线程池
        CoAPClientThreadPool clientThreadPool = new CoAPClientThreadPool();

        // 3.back、启动client
//        PureDtlsCoapClientNormal client = new PureDtlsCoapClientNormal();
//        client.startInThread();


        // 4、测试消息发送



        for (int i = 0; i < 99; i ++) {
            OutCoAPMessage message = new OutCoAPMessage(("this is data" + i + " .").getBytes());
            DTLSMessageList.addDTLSMessage(message);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (;;) {

            try {
                Thread.sleep(1000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("---------" + DTLSMessageList.getDTLSMessageAmount() + "---------");
        }
    }
}
