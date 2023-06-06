//package com.communication.dtls.dtls;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author: bin
// * @Date: 2019/9/26 15:56
// * @Description:
// * 采用单例模式
// */
//public class MessageList {
//    // 正在发送的 帧
//    private static List<MessageFragment> sendingList = new ArrayList<>();
//    // 接收到的消息，在此存放，准备处理。
//    private static List<CoAPMessage> preHandList = new ArrayList<>();
//    // 发送后的消息 有回调的消息在此存放，等待回调
//    private static List<CoAPMessage> preCallBackList = new ArrayList<>();
//    // 正在接收的消息
//    private static List<MessageCombiner> receivingList = new ArrayList<>();
//    // 用户准备发送的message
//    private static List<CoAPMessage> preSendList = new ArrayList<>();
//
//    private MessageList() {}
//
//    public static List<MessageFragment> getSendingList() {
//        return sendingList;
//    }
//    public static List<CoAPMessage> getPreHandList() {
//        return preHandList;
//    }
//    public static List<CoAPMessage> getPreCallBackList() {
//        return preCallBackList;
//    }
//    public static List<MessageCombiner> getReceivingList() {
//        return receivingList;
//    }
//
//    public static List<CoAPMessage> getPreSendList() {
//        return preSendList;
//    }
//
//    // 根据 uuid 查找 receivingList 中，唯一 MessageCombiner
//    public static MessageCombiner getReceivingCombinerByUuid(String uuid) {
//        while (receivingList.iterator().hasNext()) {
//            MessageCombiner combiner = receivingList.iterator().next();
//            if (combiner.getUuid().equals(uuid)) {
//                return combiner;
//            }
//        }
//        System.out.println("没有找到 相应的 combiner");
//        return null;
//    }
//
//    // 根据 uuid 发送方 接收方 查找 preCallBackList 中，唯一 Message
//    public static CoAPMessage getCallBackByUuid(String uuid) {
//        while (preCallBackList.iterator().hasNext()) {
//            CoAPMessage message = preCallBackList.iterator().next();
//            if (message.getUuid().equals(uuid)) {
//                preCallBackList.remove(message);
//                return message;
//            }
//        }
//        System.out.println("没有找到 相应的 可回调的内容");
//        return null;
//    }
//}
