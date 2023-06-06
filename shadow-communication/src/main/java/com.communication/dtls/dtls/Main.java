//package com.communication.dtls.dtls;
//
//import com.communication.coap.CoAPUtil;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.util.List;
//
///**
// * @Author: bin
// * @Date: 2019/9/26 15:43
// * @Description:
// */
//public class Main {
//    private static org.apache.log4j.Logger log = com.cislc.shadow.log.Logger.getLogger(Main.class);
////    private static final String receiverAddr = "211.87.235.74";
////    private static final String receiverAddr = "fe80::b988:9f46:db7e:fd8a%11";
//    private static final String receiverAddr = "127.0.0.1";
//
//
//    public static void main(String[] args) throws Exception {
//        InetSocketAddress receiver = new InetSocketAddress(receiverAddr, 5684);
//        // 启动一个server
//        CoAPServer server = new CoAPServer();
//        server.startAServer();
//
//        // 启动一个client
////        CoAPClient client = new CoAPClient();
////        client.startAClient();
////        Thread.sleep(5000);
//
////        // 发送一个图片
////        File file = new File(CoAPUtil.TEST_IN_IMAGE_PATH);
////        byte[] fileBytes = new byte[(int)file.length()];
////        MessageList.getPreSendList().add(new CoAPMessage(fileBytes, receiver));
////        // 发送一个视频
////        File file1 = new File(CoAPUtil.TEST_IN_FILE_PATH);
////        byte[] fileBytes1 = new byte[(int)file1.length()];
////        byte[] fileBytes1 = "a request from main".getBytes();
////        new CoAPMessage(fileBytes1, receiver, CoAPUtil.DONT_RETURN).send();
////        MessageList.getPreSendList().add();
//
//
//
//        while (true) {
//            try {
//                Thread.sleep(2000);
//                List<CoAPMessage> preHandList = MessageList.getPreHandList();
////                System.out.println(" getSendingList's size is " + MessageList.getSendingList().size());
////                System.out.println(" getPreHandList's size is " + MessageList.getPreHandList().size());
////                System.out.println(" getPreCallBackList's size is " + MessageList.getPreCallBackList().size());
////                System.out.println(" getReceivingList's size is " + MessageList.getReceivingList().size());
////                System.out.println(" getPreSendList's size is " + MessageList.getPreSendList().size());
////                if (MessageList.getReceivingList().size() > 0) {
////                    System.out.println("MessageList.getReceivingList().get(0).getReceivedFragmentNum() is " + MessageList.getReceivingList().get(0).getReceivedFragmentNum());
////                }
//                if (preHandList.size() > 0) {
//                    CoAPMessage message = preHandList.get(0);
//                    OutputStream out = new FileOutputStream(CoAPUtil.TEST_OUT_FILE_PATH);
//                    out.write(message.getBody());
//                    out.close();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//
//
//
//    }
//
//}
