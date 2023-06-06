package com.shadow.web.utils;

import com.shadow.web.model.xml.FieldDetail;

import static com.shadow.web.consts.JavaCodeConstants.*;

/**
 * 设备属性生成工具
 *
 * @author szh
 * @since 2021/5/4 23:40
 **/
public class DeviceAttrUtils {

    private static final FieldDetail deviceIdAttr;
    private static final FieldDetail topicAttr;
    private static final FieldDetail ipAttr;
    private static final FieldDetail bindCodeAttr;

    static {
        deviceIdAttr = new FieldDetail("String", COMMON_PROPERTY_DEVICE_ID, "设备ID");
        topicAttr = new FieldDetail("String", COMMON_PROPERTY_TOPIC, "通信主题");
        ipAttr = new FieldDetail("String", COMMON_PROPERTY_IP, "ip");
        bindCodeAttr = new FieldDetail("String", COMMON_PROPERTY_BIND_CODE, "绑定码");
    }

    public static FieldDetail getDeviceIdAttr() {
        return deviceIdAttr;
    }

    public static FieldDetail getTopicAttr() {
        return topicAttr;
    }

    public static FieldDetail getIpAttr() {
        return ipAttr;
    }

    public static FieldDetail getBindCodeAttr() {
        return bindCodeAttr;
    }

}
