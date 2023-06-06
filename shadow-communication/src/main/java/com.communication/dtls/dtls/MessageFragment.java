//package com.communication.dtls.dtls;
//
//import java.net.InetSocketAddress;
//
//import static com.communication.coap.CoAPUtil.*;
//
///**
// * @Author: bin
// * @Date: 2019/10/8 15:02
// * @Description:
// * 需要维护两个版本
// * 1、使用滑动窗口协议
// * 2、不使用滑动窗口协议
// */
//
//public class MessageFragment {
//
//    // 消息的唯一标识， 用于消息的跟踪组装。
//    private String uuid;
//    // 当前帧的 编号
//    private int currentFrame;
////    // 总帧数
////    private int totalFrame;
////    // 消息体的长度
////    private int bodyLength;
////    // 消息体
////    private byte[] body;
//    // 接收方是否已经接收到此消息
//    private boolean isRecived;
//    // 文件或byte数组起始的位置，方便重传
//    private long startPos;
//    // 方便重传时查找
//    private CoAPMessage parent;
//
//    private InetSocketAddress receiverAddr;
//
//    // 告诉server 必须从server返回
//    private byte returnFromServer;
//
//    private byte[] content = new byte[MAX_PLAINTEXT_FRAGMENT_LENGTH];
//
//    public MessageFragment(InetSocketAddress receiverAddr, byte[] content) {
//        // TODO 从content中拿到uuid等信息
//
//        this.receiverAddr = receiverAddr;
//        this.content = content;
//    }
//
//    public MessageFragment(String uuid, int currentFrame, int totalFrame, int bodyLength, byte[] body, InetSocketAddress receiverAddr, byte returnOption) {
//        // 对方收到信息后才能删除此消息，以下三条用于跟踪
//        this.uuid = uuid;
//        this.currentFrame = currentFrame;
//        this.isRecived = false;
//        this.receiverAddr = receiverAddr;
//        // TODO (暂不)使用滑动窗口协议 时， 这里应有 uuid，当前帧号，是否最后一帧，消息体长度，消息内容。
//        content[RETURN_OPTION_START] = returnOption;
//        // combiner 组装时，认为已接收到此分组
//        content[STATE_BYTE_START] = IS_RECEIVED;
//        // 封装uuid
//        System.arraycopy(uuid.getBytes(), 0,content, UUID_START, UUID_LENGTH);
//        // 封装 当前帧号
//        System.arraycopy(transIntToBytes(currentFrame), 0, content, CURRENT_FRAME_START, CURRENT_FRAME_LENGTH);
//        // 封装 总帧数
//        System.arraycopy(transIntToBytes(totalFrame), 0, content, TOTAL_FRAME_START, TOTAL_FRAME_LENGTH);
//        // 封装 消息体的长度信息
//        System.arraycopy(transIntToBytes(bodyLength), 0, content, BODY_LENGTH_START, BODY_LENGTH_LENGTH);
//        // 封装 消息体的内容
//        System.arraycopy(body, 0, content, BODY_START, bodyLength);
//    }
//
//    public MessageFragment(byte[] content) {
//        this.content = content;
//    }
//
//    public String getUuid() {
//        return this.uuid;
//    }
//
//    public String getUuidFromBytes() {
//        byte[] uuid = new byte[UUID_LENGTH];
//        System.arraycopy(content, UUID_START, uuid, 0, UUID_LENGTH);
//        return new String(uuid);
//    }
//
//    public int getCurrentFrameFromBytes() {
//        byte[] currentFrame = new byte[CURRENT_FRAME_LENGTH];
//        System.arraycopy(content, CURRENT_FRAME_START, currentFrame, 0, CURRENT_FRAME_LENGTH);
//        return transBytesToInt(currentFrame);
//    }
//
//    public int getTotalFrame() {
//        byte[] totalFrame = new byte[CURRENT_FRAME_LENGTH];
//        System.arraycopy(content, TOTAL_FRAME_START, totalFrame, 0, TOTAL_FRAME_LENGTH);
//        return transBytesToInt(totalFrame);
//    }
//
//    public int getBodyLength() {
//        byte[] bodyLength = new byte[BODY_LENGTH_LENGTH];
//        System.arraycopy(content, BODY_LENGTH_START, bodyLength, 0, BODY_LENGTH_LENGTH);
//        return transBytesToInt(bodyLength);
//    }
//
//    public byte[] getContent() {
//        return content;
//    }
//
//
//    public InetSocketAddress getReceiverAddr() {
//        return receiverAddr;
//    }
//
//    public void setReceiverAddr(InetSocketAddress receiverAddr) {
//        this.receiverAddr = receiverAddr;
//    }
//}
