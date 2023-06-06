//package com.communication.coap;
//
//import com.communication.common.OutMessage;
//import com.communication.common.Protocol;
//
//import java.net.InetSocketAddress;
//
///**
// * @Author: bin
// * @Date: 2019/10/30 15:28
// * @Description:
// */
//public class CoAPClient {
//    public static final String server_host = "192.168.3.141";
//
//    public static void main(String[] args) {
//
//
//        /** {@link OutCoAPMessage} 需要手动添加至发送列表 */
////        OutCoAPMessage message = new OutCoAPMessage("this is a payload ,".getBytes(), "hello", new InetSocketAddress(server_host, 5684));
////        // 添加message时，会自动发送
////        CoAPMessageList.addCoAPMessage(message);
//
//        /** {@link OutMessage} 会自动发送*/
//        OutMessage message = new OutMessage(Protocol.COAP, "this is a payload ,".getBytes(), "hello", new InetSocketAddress(server_host, 5684));
//
//
//        for (;;) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//}
