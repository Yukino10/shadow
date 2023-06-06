package com.communication.coap;

import java.net.InetSocketAddress;

/**
 * @Author: bin
 * @Date: 2019/10/23 13:16
 * @Description:
 *
 * 发送消息：
 * 1、构造 OutMessage 对象。（并校验消息体的长度）
 * 2、根据 receiverAddr， 到内存中查询receiver的通信信息（协议，接收消息的入口）
 *      注： 在项目启动时，从数据库加载所有的receiver通信信息到内存。
 * 3、将senderId、requestUrl、data 格式化为 byte[]后准备发送
 * 4、调用receiver通信信息支持的协议对应的client 发送消息
 *
 * 应答消息：
 * client 向 server 请求消息，server返回的消息称为应答消息。此类消息不一定有requestUrl
 *
 * 接收消息：
 * 接收消息时，可以拿到发送方的ip、端口等信息，也有可能拿不到。
 * 1、解析消息体byte[]中 senderAddr、requestUrl、data 信息。
 * 2、将 senderAddr、data 封装成 #{@link InCoAPMessage }
 * 3、根据requestUrl 找到该信息需调用的方法并执行。
 *
 * 关于data[]：
 * 各接收InMessage的服务在接收到data时， 可以 将data[]先变成对象，然后根据对象提供一些服务。
 * 比如：  1、重定向到其他服务
 *         2、在某个服务运算后，将结果返回至指定接口
 *         3、其他灵活多变的服务
 *         4、甚至可以将ACK也写成一个服务。
 */
// TODO byte[] data 需要重新指定一下规范，方便接收后解析。
@Deprecated
public class CoAPMessage {
    // 发送方的ip+端口
    private InetSocketAddress senderAddr;
    // 接收方的ip+端口
    private InetSocketAddress receiverAddr;

    private byte[] data;
    // 请求的url
    private String requestUrl;



    CoAPMessage() {
    }

    public InetSocketAddress getSenderAddr() {
        return senderAddr;
    }

    void setSenderAddr(InetSocketAddress senderAddr) {
        this.senderAddr = senderAddr;
    }

    public InetSocketAddress getReceiverAddr() {
        return receiverAddr;
    }

    void setReceiverAddr(InetSocketAddress receiverAddr) {
        this.receiverAddr = receiverAddr;
    }

    public byte[] getData() {
        return data;
    }

    void setData(byte[] data) {
        this.data = data;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

     void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }




//    /**
//     * 根据设备编号，获取设备的ip等信息。准备进行消息发送
//     * @return
//     */
//    public InetSocketAddress getSocketAddress() {
//
////        return new InetSocketAddress("127.0.0.1", 5684);
//        return new InetSocketAddress("192.168.1.158", 5684);
//    }


}
