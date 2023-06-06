package com.communication.coap;


import com.communication.common.CommunicationService;
import com.communication.common.DeviceAddr;
import com.communication.common.ShadowCallBack;

import java.net.InetSocketAddress;


/**
 * @Author: bin
 * @Date: 2019/10/23 13:15
 * @Description:
 *
 * 使用 {@link CommunicationService } 标注的 方法的入参
 */
public class InCoAPMessage {

    private DeviceAddr senderAddr;
    private byte[] data;

//    private String senderId;
//
//    private byte[] data;

    // 将 源ip+端口  根据相应的对应关系，转换为 senderId
    // 根据 requestUrl 根据相应的规则  将此 InMessage 放入某接口
    // 接口处理请求后，根据情况返回 OutMessage
    // 如果 OutMessage 不为 null，返回上层。


    public InCoAPMessage(byte[] data) {
        this.data = data;
    }
    public InCoAPMessage(DeviceAddr senderAddr, byte[] data) {
        // 一般拿不到发送方的地址，都是内网地址
        this.senderAddr = senderAddr;
        this.data = data;
    }


    public byte[] getData() {
        return data;
    }


}
