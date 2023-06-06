package com.communication.common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: bin
 * @Date: 2019/10/23 17:05
 * @Description:
 */
public class MethodMap {

    private static Map<String, MethodInfo> methodMap = new HashMap<>();

    public static void addMethod(String url, MethodInfo methodInfo) {
        if (methodMap.get(url) != null) {
            System.out.println("reduplicated url is no allowed , please check url which address is " + url);
            return;
        }
        methodMap.put(url, methodInfo);
    }

    public static MethodInfo getMethod(String url) {
        return methodMap.get(url);
    }

    public static Map<String, MethodInfo> getAllMethod() {
        return methodMap;
    }
}
