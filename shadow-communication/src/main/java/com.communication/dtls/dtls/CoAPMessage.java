//package com.communication.dtls.dtls;
//
//import com.communication.coap.CoAPUtil;
//
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author: bin
// * @Date: 2019/10/14 15:55
// * @Description:
// */
//public class CoAPMessage {
//    // uuid
////     返回信息的位置
//    // 发送者 信息
//    // 消息体，长度小于256M
//    // 消息应该谁来处理？ handler
//
//    private String uuid;
//
//    private InetSocketAddress senderAddr;
//
//    private InetSocketAddress receiverAddr;
//    // 标识，对方哪个接口执行这些数据。
//    private String handlerUrl;
//
//    private byte[] body;
//
//    // 标识此消息 需要对方从何处返回或是否需要返回
//    private byte returnOption;
////  这个应该交给下一层判断
////    private InetSocketAddress handlerAddr;
//
//    // callback函数等信息。
//
//
//
//    // 默认只有消息体和接收方
//    public CoAPMessage(byte[] body, InetSocketAddress receiverAddr, byte returnOption) {
//        this.body = body;
//        this.receiverAddr = receiverAddr;
//        this.uuid = CoAPUtil.createUuid();
//    }
//
//    /**
//     * 用户调用此方法
//     */
//    public void send() {
//        if (this.body == null) {
//            System.out.println("空指针异常");
//            return;
//        }
//        int totalFrame = this.body.length / CoAPUtil.MAX_SEND_LENGTH + (this.body.length % CoAPUtil.MAX_SEND_LENGTH == 0 ? 0 : 1);
//        System.out.println("------------- total frame num is " + totalFrame + "------------");
//        MessageDivider divider = new MessageDivider(this.uuid, totalFrame, receiverAddr, body, returnOption);
//        divider.sendAllMessage();
////        MessageList.getSendingList().addAll(divideMessage());
//    }
//
//    // 当上层调用send时，此方法将消息拆解后，放入list
//    List<MessageFragment> divideMessage() {
//        List<MessageFragment> result = new ArrayList<>();
//        // 如果不需要分组
//        if (this.body.length < CoAPUtil.MAX_SEND_LENGTH) {
//            MessageFragment fragment = new MessageFragment(this.uuid, 1, 1, this.body.length, this.body, this.receiverAddr, returnOption);
//            result.add(fragment);
//        } else {
//            int totalFrame = this.body.length / CoAPUtil.MAX_SEND_LENGTH + (this.body.length % CoAPUtil.MAX_SEND_LENGTH == 0 ? 0 : 1);
//            int currentFrame = 0;
//            for (; currentFrame < totalFrame - 1; currentFrame ++) {
//                byte[] data = new byte[CoAPUtil.MAX_PLAINTEXT_FRAGMENT_LENGTH];
//                System.arraycopy(this.body,currentFrame * CoAPUtil.MAX_SEND_LENGTH, data, 0, CoAPUtil.MAX_SEND_LENGTH);
//                MessageFragment fragment = new MessageFragment(this.uuid, currentFrame, totalFrame, CoAPUtil.MAX_SEND_LENGTH, data, this.receiverAddr, returnOption);
//                result.add(fragment);
//            }
//            int lastLength = this.body.length - currentFrame * CoAPUtil.MAX_SEND_LENGTH;
//            byte[] data = new byte[lastLength];
//            System.arraycopy(this.body,currentFrame * CoAPUtil.MAX_SEND_LENGTH, data, 0, lastLength);
//            MessageFragment fragment = new MessageFragment(this.uuid, currentFrame, totalFrame, lastLength, data, this.receiverAddr, returnOption);
//            result.add(fragment);
//        }
//        System.out.println("-----------pre send list size is " + result.size());
//        return result;
//    }
//
//    public String getHandlerUrl() {
//        return handlerUrl;
//    }
//
//    public void setHandlerUrl(String handlerUrl) {
//        this.handlerUrl = handlerUrl;
//    }
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public InetSocketAddress getSenderAddr() {
//        return senderAddr;
//    }
//
//    public void setSenderAddr(InetSocketAddress senderAddr) {
//        this.senderAddr = senderAddr;
//    }
//
//    public InetSocketAddress getReciverAddr() {
//        return receiverAddr;
//    }
//
//    public void setReciverAddr(InetSocketAddress reciverAddr) {
//        this.receiverAddr = reciverAddr;
//    }
//
//    public byte[] getBody() {
//        return body;
//    }
//
//    public void setBody(byte[] body) {
//        this.body = body;
//    }
//}
