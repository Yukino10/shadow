package com.communication.dtls.pureDtls;

import org.eclipse.californium.elements.EndpointContext;
import org.eclipse.californium.elements.MessageCallback;
import org.eclipse.californium.scandium.DTLSConnector;

/**
 * @Author: bin
 * @Date: 2019/10/25 14:37
 * @Description:
 * {@link DTLSConnector } 发送完一条消息后的回调函数。
 * 当然是发下一条啦。
 */
public class CoAPMessageCallback implements MessageCallback {
    DTLSConnector dtlsConnector;
    PureDtlsCoapClient client;

    public CoAPMessageCallback(DTLSConnector dtlsConnector, PureDtlsCoapClient client) {
        this.dtlsConnector = dtlsConnector;
        this.client = client;
    }


    @Override
    public void onConnecting() {
//        System.out.println(Thread.currentThread()established.getName() + " on connecting.");
    }

    @Override
    public void onDtlsRetransmission(int flight) {

        System.out.println(Thread.currentThread().getName() + " on dtls retransmission.");
    }

    @Override
    public void onContextEstablished(EndpointContext context) {

//        System.out.println(Thread.currentThread().getName() + " on context established.");
    }

    @Override
    public void onSent() {
        // 重新设置此client为可用状态
        System.out.println(Thread.currentThread().getName() + " on sent message. ");
        client.isUseAble.set(true);

//        if ((message = CoapMessageList.getDTLSMessage()) != null) {
//            RawData data = RawData.outbound(message.getData(), new AddressEndpointContext(message.getSocketAddress()),
//                    new CoAPMessageCallback(dtlsConnector), false);
//            System.out.println(Thread.currentThread().getName() + " client send message -- on sent -- " + new String(message.getData()) + " -- current message amount " + CoapMessageList.getDTLSMessageAmount());
//            dtlsConnector.send(data);
//        } else {
//            System.out.println(Thread.currentThread().getName() + ": str is null !" + " -- current message amount " + CoapMessageList.getDTLSMessageAmount());
////            try {
////                // 不知道这里让线程休眠会不会影响消息的接收
////                Thread.sleep(500);
////                onSent();
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }

    }

    @Override
    public void onError(Throwable error) {

        System.out.println(Thread.currentThread().getName() + " on error.");
    }
}
