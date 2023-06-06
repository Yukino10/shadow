package com.cislc.shadow.utils.bean.comm;

import com.cislc.shadow.utils.enums.Protocol;
import com.cislc.shadow.utils.enums.Encryption;

/**
 * @ClassName CommParams
 * @Description 影子通信参数
 * @Date 2019/11/25 10:06
 * @author szh
 **/
public class CommParams {

    private Protocol protocol;
    private String ip;
    private String topic;
    private Encryption encryption;
    private String encryptKey;

    public CommParams() {}

    public CommParams(Protocol protocol, String ip, String topic, Encryption encryption, String encryptKey) {
        this.protocol = protocol;
        this.ip = ip;
        this.topic = topic;
        this.encryption = encryption;
        this.encryptKey = encryptKey;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

}
