package com.communication.dtls.pureDtls;

import com.communication.coap.OutCoAPMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: bin
 * @Date: 2019/10/23 20:12
 * @Description:
 * 只有等待发送的 消息会放在这里
 * 不对，因为超时重传，响应的消息也在这里么？如果对方未收到，会再次请求。
 * 也不对，如果我方未收到对方确认，会触发超时重传。
 * 还有些问题， 对方发起请求，我方收到后，会回复 ack，然后再处理。
 * 这样就引发了一个问题，如果我收到请求，我不知道这是对方第一个请求还是超时重传的请求，我可以在信息中加标志位或遍历消息列表。
 * 我认为加标志位在消息量大时非常好，还能预测丢包率，进行拥塞控制。
 *
 * 所以，此处得到一结论： 超时请求的时间一定要小于 超时重传时间。
 * 一般的， 超时请求时间 > rtt * 2 + 系统处理时间
 *
 * TODO 超时重传和拥塞控制都不需要担心，这些消息一经发送，都是确保交付的。
 */

/**
 * 准备发送的消息都在这
 */
public class DTLSMessageList {

    private static List<OutCoAPMessage> dtlsMessageList = new ArrayList<>();


    public static OutCoAPMessage getDTLSMessage() {
        synchronized (DTLSMessageList.class) {
            if (dtlsMessageList.size() > 0) {
                return dtlsMessageList.remove(0);
            }
            return null;
        }
    }

    public static void addDTLSMessage(OutCoAPMessage message) {
        synchronized (DTLSMessageList.class) {
            dtlsMessageList.add(message);
            CoAPClientThreadPool.callUpAClient();
        }
        // 添加完消息后，将暂停的线程启动(一个即可)。如果没有暂停的就算了
    }

    public static void addAllDTLSMessage(List<OutCoAPMessage> messages) {
        synchronized (DTLSMessageList.class) {
            dtlsMessageList.addAll(messages);
            CoAPClientThreadPool.callUpSomeClient();
        }
        // 添加完消息后，将暂停的线程启动(启动多个)。
        // 判断 list 的总长度，判断是否需要根据规则开启新线程
    }


    public static int getDTLSMessageAmount() {
        return dtlsMessageList.size();
    }

}
