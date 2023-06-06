package com.communication.mqtt;

import com.communication.common.ShadowCallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: bin
 * @Date: 2019/11/11 16:35
 * @Description:
 */
public class MQTTCallBackMap {
    static Map<String, ShadowCallBack> callBackMap = new HashMap<>();

    public static void putCallBack(String uuid, ShadowCallBack callBack) {
        callBackMap.put(uuid, callBack);
    }

    public static ShadowCallBack getCallBack(String uuid) {
        return callBackMap.get(uuid);
    }
}
