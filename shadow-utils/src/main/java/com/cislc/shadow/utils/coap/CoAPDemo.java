package com.cislc.shadow.utils.coap;

import org.apache.log4j.Logger;
/**
 * @Author: bin
 * @Date: 2019/11/20 8:53
 * @Description:
 */
public class CoAPDemo {

    private static Logger log = Logger.getLogger(CoAPClient.class);
    public static void main(String[] args) {
        binTestFunction();
    }


    static void binTestFunction() {
        // 启动server
        CoAPClient.startServer((String inMessage) -> {
            // 当server收到消息时的逻辑...
            System.out.println("receive a message --> " + inMessage);
            return null;
        });
        CoAPClient.setMessageExceptionHandler((ip, message) -> {
            System.out.println("发送至 " + ip + " 的消息： " + message + " 没有收到。");
        });

        // 使用client发送一条消息
        CoAPClient.sendMessage("127.0.0.1", "this is a test message");
    }
}
