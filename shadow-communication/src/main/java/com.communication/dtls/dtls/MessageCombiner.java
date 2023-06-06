//package com.communication.dtls.dtls;
//
//import com.communication.coap.CoAPUtil;
//
//import static com.communication.coap.CoAPUtil.*;
//
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * @Author: bin
// * @Date: 2019/10/8 15:03
// * @Description:
// * 分块传输 信息的组装器
// * 只管确保整个消息的完整性， 不管消息的用途。，消息的用途等信息，需进行再次封装。
// */
//public class MessageCombiner {
//    private String uuid;
//
//    // 分块传输的信息素组。
//    private byte[][] response;
//    // 开始接收时间，也就是创建时间
//    private Date createTime;
//
//    // 上一次接收时间
//    private Date updateTime;
//    // 接收到的分组数
//    private int receivedFragmentNum;
//    // fragment总数
//    private int totalFragmentNum;
//    // 此message的总长度
//    private int totalLength;
//    // 发送方信息
//    private InetSocketAddress senderAddr;
//
//    public MessageCombiner(String uuid, int totalFragmentNum, InetSocketAddress senderAddr) {
//        this.uuid = uuid;
//        this.totalFragmentNum = totalFragmentNum;
//        this.receivedFragmentNum = 0;
//        this.createTime = new Date();
//        this.response = new byte[totalFragmentNum + 1][MAX_PLAINTEXT_FRAGMENT_LENGTH];
//        this.senderAddr = senderAddr;
//    }
//
////    public void initResponse(int totalFragmentNum) {
////        this.totalFragmentNum = totalFragmentNum;
////        this.receivedFragmentNum = 0;
////        this.createTime = new Date();
////        this.response = new byte[totalFragmentNum][MAX_PLAINTEXT_FRAGMENT_LENGTH];
////    }
//
//
//    public InetSocketAddress getSenderAddr() {
//        return senderAddr;
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
//    /**
//     * 所有数据接收完毕后，对数据进行合并
//     * @return
//     */
//    public byte[] getAllData() {
//        // 先获得data的总长度
//        int length = 0;
//        for (int i = 0; i < totalFragmentNum; i ++) {
//            length += getDataLength(response[i]);
//        }
//        // 初始化 结果 数组
//        byte[] result = new byte[length];
//        // 每次拷贝时，destPos都会增加
//        int position = 0;
//        for (int i = 0; i < totalFragmentNum; i ++) {
//            int currentFragmentLength = getDataLength(response[i]);
//            System.arraycopy(response[i], BODY_START, result, position, currentFragmentLength);
//            position += currentFragmentLength;
//        }
//        return result;
//    }
//
//    /**
//     * 判断此数据在接收过程中是否超时，以便请求重发
//     * @return
//     * TODO 暂时不用这里判断，从client端 ack 判断
//     */
//    public boolean isOverTime() {
////        if (updateTime.getTime() + CoAPUtil.ti)
//        return true;
//    }
//
//    /**
//     * 获取所有未收到的数据包的编号
//     *
//     * 如果全部收到，则返回null
//     */
//    public List<Integer> getUnReceivedNum() {
//        List<Integer> result = new ArrayList<>();
//        for (int i = 0; i < this.totalFragmentNum; i ++) {
//            if (this.response[i][0] != IS_RECEIVED) {
//                result.add(i);
//            }
//        }
//
//        return result;
//    }
//
//    /**
//     * 保存一个数据包, 如果之前保存过， 则丢弃新的
//     * @param data
//     * 返回 此次保存的分组编号，以便向发送方响应。
//     */
//    // TODO 这里应保证线程安全， 包住 保存帧 和 增加收到的分组数
//    public int receiveData(byte[] data) {
//        byte[] currentFrameBytes = new byte[CURRENT_FRAME_LENGTH];
//        // 获取帧的编号
//        int frameNum = transBytesToInt(currentFrameBytes);
//        // 如果当前帧已接收
//        if (response[frameNum][0] == IS_RECEIVED) {
//            return frameNum;
//        }
//        // 保存当前帧
//        response[frameNum] = data;
//        // 接收到的分组数 + 1
//        receivedFragmentNum ++;
//        // 更新最新接收时间
//        updateTime = new Date();
//        return frameNum;
//    }
//    public int receiveData(MessageFragment fragment) {
//        // 获取帧的编号
//        int frameNum = fragment.getCurrentFrameFromBytes();
//        // 如果当前帧已接收
//        if (response[frameNum][0] == IS_RECEIVED) {
//            return frameNum;
//        }
//        // 保存当前帧
//        response[frameNum] = fragment.getContent();
//        // 接收到的分组数 + 1
//        receivedFragmentNum ++;
//        // 更新最新接收时间
//        updateTime = new Date();
//        return frameNum;
//    }
//
//    private int getDataLength(byte[] bytes) {
//        byte[] bodyLength = new byte[BODY_LENGTH_LENGTH];
//        System.arraycopy(bytes, BODY_LENGTH_START, bodyLength, 0, BODY_LENGTH_LENGTH);
//        return transBytesToInt(bodyLength);
//    }
//
//    public int getReceivedFragmentNum() {
//        return receivedFragmentNum;
//    }
//
//    public int getTotalFragmentNum() {
//        return totalFragmentNum;
//    }
//}
