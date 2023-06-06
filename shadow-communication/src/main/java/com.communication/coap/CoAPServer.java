package com.communication.coap;

import com.communication.common.*;
import com.communication.utils.CommonUtil;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author: bin
 * @Date: 2019/10/30 15:28
 * @Description:
 */
public class CoAPServer {


    /**
     * 在此之前应完成扫包
     */
    public static void start() {
        //  TODO 1、自检 检查是否存在外网ip

        // 2、启动server
        CoapServer server = new CoapServer(CommonUtil.COAP_PORT);

        // 为server添加resource
        for (String url : MethodMap.getAllMethod().keySet()) {
            MethodInfo method = MethodMap.getMethod(url);
            System.out.println("a url : " + url + " will be add. ");
            server.add(new CoapResource(url) {
                @Override
                public void handleRequest(final Exchange exchange) {

                    byte[] request = exchange.getRequest().getBytes();
                    InCoAPMessage inCoAPMessage = new InCoAPMessage(request);
                    System.out.println("coAP server receive a message : " + new String(inCoAPMessage.getData()));
                    try {
                        if (!method.getMethod().getReturnType().equals("void")) {
                            OutMessage outMessage = (OutMessage) method.getMethod().invoke(method.getObject(), MessageTransUtil.transCoAPToInMessage(inCoAPMessage));
                            OutCoAPMessage outCoAPMessage = MessageTransUtil.transOutMessageToCoAP(outMessage);
                            Response response = new Response(CoAP.ResponseCode.CONTENT);
                            response.setPayload(outCoAPMessage.getData());
                            System.out.println(Thread.currentThread().getName() + " server request a message ,-- " + new String(outCoAPMessage.getData()));

                            exchange.sendResponse(response);
                        } else {
                            method.getMethod().invoke(method.getObject(), inCoAPMessage);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        server.start();
        System.out.println("server started.");
    }
}
