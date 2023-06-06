package com.cislc.shadow.manage.core.bean.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.cislc.shadow.manage.common.utils.UUIDUtils;
import com.cislc.shadow.manage.core.shadow.EntityFactory;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @ClassName ShadowEntity
 * @Description 影子实体
 * @Date 2019/10/15 14:53
 * @author szh
 **/
@Slf4j
@MappedSuperclass
public class ShadowEntity extends ShadowSubject implements Serializable {

    /**
     * Shadow Resource Identifier
     * 影子资源标识符
     */
    @Id
    @JSONField(name = "sri")
    @Column(name = "sri")
    private String sri;

    /**
     * 实体所属的影子对象的UUID
     */
    @JSONField(serialize = false)
    @Transient
    private String deviceId;

    /**
     * 绑定标识码
     */
    @JSONField(serialize = false)
    @Transient
    private String bindCode;

    /**
     * 使用MQTT通信的设备topic
     */
    @JSONField(serialize = false)
    @Transient
    private String topic;

    /**
     * 使用COAP通信设备的ip
     */
    @JSONField(serialize = false)
    @Transient
    private String ip;

    /**
     * 实体是否是设备
     */
    @JSONField(serialize = false)
    @Transient
    public boolean isDevice() {
        return false;
    }

    /**
     * 通信协议
     */
    @JSONField(serialize = false)
    @Transient
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @JSONField(serialize = false)
    @Transient
    public Encryption getEncryption() {
        return Encryption.NONE;
    }

    public ShadowEntity() {
        generateSRI();
        setDeviceId(UUIDUtils.getUUID());
    }

    /**
     * @Description 初始化生成SRI并注入容器
     * @author szh
     * @Date 2019/6/16 19:54
     */
    public ShadowEntity(String deviceId) {
        super();
        setDeviceId(deviceId);
        generateSRI();
        EntityFactory.injectEntity(this);
    }

    /**
     * @Description 生成影子SRI
     * @author szh
     * @Date 2019/6/16 19:37
     */
    public void generateSRI() {
        /*
         * 实体sri三部分：
         * 1. 当前实体类名
         * 2. 13位时间戳
         * 3. 3位随机数，不足3位前面补0
         */
        int random = (int) (Math.random() * 1000);
        this.sri = this.getClass().getSimpleName() + "_" +
                System.currentTimeMillis() + "_" +
                String.format("%03d", random);
    }

    /**
     * @Description sri是否合法
     * @author szh
     * @Date 2019/8/13 10:28
     */
    public boolean checkSRI() {
        if (StringUtils.isEmpty(this.sri)) {
            return false;
        }
        String[] sriPart = this.sri.split("_");
        // 校验sri三部分
        return 3 == sriPart.length &&
                this.getClass().getSimpleName().equals(sriPart[0]) &&
                Pattern.matches("^[0-9]{13}$", sriPart[1]) &&
                Pattern.matches("^[0-9]{3}$", sriPart[2]);

    }

    public void setSri(String sri) {
        this.sri = sri;
    }

    public String getSri() {
        return sri;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBindCode() {
        return bindCode;
    }

    public void setBindCode(String bindCode) {
        this.bindCode = bindCode;
    }

}
