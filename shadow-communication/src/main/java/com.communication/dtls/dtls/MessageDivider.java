//package com.communication.dtls.dtls;
//
//import com.communication.coap.CoAPUtil;
//
//import java.net.InetSocketAddress;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Author: bin
// * @Date: 2019/10/16 9:52
// * @Description:
// */
//public class MessageDivider {
//    private String uuid;
//
//    // 分块传输的信息素组。
//    private byte[][] response;
//    // 开始发送时间
//    private Date createTime;
//
//    // 上一次收到对方的确认消息的时间
//    private Date updateTime;
//    // 对方接收到的分组数
//    private int receivedFragmentNum;
//    // fragment总数
//    private int totalFragmentNum;
//    // 此message的总长度
//    private int totalLength;
//    // 接收方信息
//    private InetSocketAddress receiverAddr;
//    // 标识此消息 需要对方从何处返回或是否需要返回
//    private byte returnOption;
//    // body 用户发送的信息
//    private byte[] body;
//
//
//    public MessageDivider(String uuid, int totalFragmentNum, InetSocketAddress receiverAddr, byte[] data, byte returnOption) {
//        this.uuid = uuid;
//        this.totalFragmentNum = totalFragmentNum;
//        this.receiverAddr = receiverAddr;
//        response = new byte[totalFragmentNum + 1][CoAPUtil.MAX_PLAINTEXT_FRAGMENT_LENGTH];
//        createTime = new Date();
//        updateTime = new Date();
//        this.returnOption = returnOption;
//        totalLength = data.length;
//        body = data;
//        divideMessage(data);
//    }
//
//
//    private void divideMessage(byte[] body) {
//        if (body.length < CoAPUtil.MAX_SEND_LENGTH) {
//            byte[] aFrame = CoAPUtil.generateFrame(this.uuid, 0, 0, body, returnOption);
//            response[totalFragmentNum] = aFrame;
//        } else {
//            // 计算总帧数 从0开始
//            int totalFrame = body.length / CoAPUtil.MAX_SEND_LENGTH + (body.length % CoAPUtil.MAX_SEND_LENGTH == 0 ? 0 : 1);
//            int currentFrame = 0;
//            for (; currentFrame < totalFrame - 1; currentFrame ++) {
//                byte[] data = new byte[CoAPUtil.MAX_PLAINTEXT_FRAGMENT_LENGTH];
//                System.arraycopy(body,currentFrame * CoAPUtil.MAX_SEND_LENGTH, data, 0, CoAPUtil.MAX_SEND_LENGTH);
//                CoAPUtil.addHeadForFrame(this.uuid, currentFrame, totalFrame, CoAPUtil.MAX_SEND_LENGTH, data, returnOption);
//                response[currentFrame] = data;
////                MessageFragment fragment = new MessageFragment(this.uuid, currentFrame, totalFrame, CoAPUtil.MAX_SEND_LENGTH, data);
////                result.add(fragment);
//            }
//            int lastLength = body.length - currentFrame * CoAPUtil.MAX_SEND_LENGTH;
//            byte[] data = new byte[lastLength];
//            System.arraycopy(body,currentFrame * CoAPUtil.MAX_SEND_LENGTH, data, 0, lastLength);
//            CoAPUtil.addHeadForFrame(this.uuid, currentFrame, totalFrame, lastLength, data, returnOption);
//            response[currentFrame] = data;
////            MessageFragment fragment = new MessageFragment(this.uuid, currentFrame, totalFrame, lastLength, data);
////            result.add(fragment);
//        }
//    }
//
//    public void sendAllMessage() {
//        List<MessageFragment> preSendList = MessageList.getSendingList();
//        for (int i = 0; i <= totalFragmentNum; i ++) {
//            MessageFragment fragment = new MessageFragment(receiverAddr, response[i]);
//            preSendList.add(fragment);
//        }
//    }
//
//    public void sendMessage(int frameNum) {
//        MessageFragment fragment = new MessageFragment(receiverAddr, response[frameNum]);
//        MessageList.getSendingList().add(fragment);
//    }
//}
