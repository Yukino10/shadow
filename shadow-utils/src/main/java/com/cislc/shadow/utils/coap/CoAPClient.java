package com.cislc.shadow.utils.coap;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: bin
 * @Date: 2019/11/19 19:23
 * @Description:
 */
public class CoAPClient {
    private static Logger log = Logger.getLogger(CoAPClient.class);
    // coap 的默认端口
    public static int port = 5683;
    public static AtomicBoolean isStarted = new AtomicBoolean(false);
    public static MessageExceptionHandler messageHandler;

    /**
     * 设置 coap发送失败时的消息处理方法
     * @param handler
     */
    public static void setMessageExceptionHandler(MessageExceptionHandler handler) {
        messageHandler = handler;
    }

    /**
     * 发送消息
     * @param ip 接收方 ip 地址
     * @param payload 消息体
     * @param callback 回调函数
     * 不推荐使用传入 byte[]的方式
     */
    @Deprecated
    public static void sendMessage(String ip, byte[] payload, CoAPCallback callback) {
        // 提醒用户设置通信异常处理办法
        if (messageHandler == null) {
            log.warn("you have not set message exception handler.");
        }
        // 确保Server启动成功后才能发消息
        while (!isStarted.get()) {
            log.info("CoAP server 没有完全启动，将在100ms后再次尝试发送消息。--> " + new String(payload));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 尝试发消息
        try {
            String uri = ip + ":" + port + "/server";
            CoapClient client = new CoapClient(uri);
            CoapResponse response = client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
            // 消息发送失败，报出异常，执行用户设置的异常处理方法
            if (response == null) {
                log.warn("message \"" + new String(payload) + "\" are not arrive to host " + ip + ", please make sure this host are reachable.");
                if (messageHandler != null) {
                    messageHandler.onSendFailure(ip, new String(payload));
                }
            }
            if (response != null && response.isSuccess() && callback != null && response.getPayload() != null) {
                System.out.println("response is " + new String(response.getPayload()) + " .");
                // 调用回调
                callback.callback(new String(response.getPayload()));
            }
        } catch (ConnectorException e) {
            log.warn("message \"" + new String(payload) + "\" occur a ConnectorException when send to host " + ip + ".");
            log.error(e.getLocalizedMessage());
            if (messageHandler != null) {
                messageHandler.onSendFailure(ip, new String(payload));
            }
        } catch (IOException e) {
            log.warn("message \"" + new String(payload) + "\" occur a IOException when send to host " + ip + ".");
            log.error(e.getLocalizedMessage());
            if (messageHandler != null) {
                messageHandler.onSendFailure(ip, new String(payload));
            }
        }
    }

    /**
     * 发送消息
     * @param ip 接收方 ip 地址
     * @param payload 消息体
     * 不推荐使用传入 byte[]的方式
     */
    @Deprecated
    public static void sendMessage(String ip, byte[] payload) {
        sendMessage(ip, payload, null);
    }

    /**
     * 发送消息
     * @param ip 接收方 ip 地址
     * @param payload 消息体
     */
    public static void sendMessage(String ip, String payload) {
        sendMessage(ip, payload.getBytes(), null);
    }

    /**
     * 发送消息
     * @param ip 接收方 ip 地址
     * @param payload 消息体
     */
    public static void sendMessage(String ip, String payload, CoAPCallback callback) {
        sendMessage(ip, payload.getBytes(), callback);
    }
    /**
     * 启动sever接收消息
     * @param callback server收到消息时的回调
     */
    public static void startServer(CoAPServerCallback callback) {

        CoapServer server = new CoapServer(port);
        server.add(new CoapResource("server") {
            @Override
            public void handleRequest(final Exchange exchange) {

                String request = exchange.getRequest().getPayloadString();
                log.info("receive a message \"" + request + "\" from host: " + exchange.getRequest().getURI().replaceAll("coap://", "").replaceAll("/server", "") + ".");
                // 收到消息,执行回调
                String backMessage = callback.callback(request);

                // 已响应的形式，发送回调函数的返回值。
                Response response = new Response(CoAP.ResponseCode.CONTENT);
                response.setPayload(backMessage);
                exchange.sendResponse(response);
            }
        });
        server.start();
        // 设置当前Server的启动状态。
        isStarted.set(true);
        log.info("coap server has stared from port: " + port);
    }

}
