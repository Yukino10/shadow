package com.communication.coap;

import com.communication.common.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.elements.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: bin
 * @Date: 2019/10/31 9:57
 * @Description:
 */
public class CoAPClientPool {

    static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new DaemonThreadFactory("CoAPClient#"));

    /**
     * 使用反射调用client 发送list中的消息
     * 只管发送，没有回调
     */
    @Deprecated
    public static void trySendMessage() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                OutCoAPMessage message = null;
                while ((message = CoAPMessageList.getCoAPMessage()) != null) {
                    String uri = message.getReceiverAddr().getCoAPAddress().getIp() + ":" + message.getReceiverAddr().getCoAPAddress().getPort() + "/" + message.getRequestUrl();
                    System.out.println("uri: " + uri);
                    CoapClient client = new CoapClient(uri);
                    try {
                        client.post(message.getData(), MediaTypeRegistry.TEXT_PLAIN);
                    } catch (ConnectorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 开启一条线程，发送一条消息
     * @param message
     */
    public static void trySendMessage(final OutCoAPMessage message) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (message != null) {
                    String uri = message.getReceiverAddr().getCoAPAddress().getIp() + ":" + message.getReceiverAddr().getCoAPAddress().getPort() + "/" + message.getRequestUrl();
                    System.out.println("uri: " + uri);
                    CoapClient client = new CoapClient(uri);
                    try {
                        client.post(message.getData(), MediaTypeRegistry.TEXT_PLAIN);
                    } catch (ConnectorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 用户主动调用client发送消息， 不会调用新线程
     * @param message
     */
    @Deprecated
    public static InCoAPMessage trySendMessageThisThread(final OutCoAPMessage message) {
        String uri = message.getReceiverAddr().getCoAPAddress().getIp() + ":" + message.getReceiverAddr().getCoAPAddress().getPort() + "/" + message.getRequestUrl();
        System.out.println("uri: " + uri);
        CoapClient client = new CoapClient(uri);
        try {
            CoapResponse response = client.post(message.getData(), MediaTypeRegistry.TEXT_PLAIN);
            if (response.isSuccess()) {
                System.out.println("response is " + response.getResponseText() + " .");
                return new InCoAPMessage(response.getPayload());
            }
        } catch (ConnectorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送coap消息并执行回调
     * @param message
     * @param callBack
     */
    public static void trySendMessage(final OutCoAPMessage message, ShadowCallBack callBack) {
        String uri = message.getReceiverAddr().getCoAPAddress().getIp() + ":" + message.getReceiverAddr().getCoAPAddress().getPort() + "/" + message.getRequestUrl();
        System.out.println("uri: " + uri);
        CoapClient client = new CoapClient(uri);
        try {
            CoapResponse response = client.post(message.getData(), MediaTypeRegistry.TEXT_PLAIN);
            if (response.isSuccess()) {
                System.out.println("response is " + response.getResponseText() + " .");
                InMessage inMessage = MessageTransUtil.transCoAPToInMessage(new InCoAPMessage(response.getPayload()));
                callBack.execute(inMessage);
            }
        } catch (ConnectorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
