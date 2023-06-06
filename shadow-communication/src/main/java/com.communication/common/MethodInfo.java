package com.communication.common;

import java.lang.reflect.Method;

/**
 * @Author: bin
 * @Date: 2019/10/23 15:42
 * @Description:
 * 用于存放被 {@link CommunicationService} 标注的方法信息。
 * object 为此方法的一个实例对象
 */
public class MethodInfo {
    Method method;
    Object object;

    MethodInfo(Method method, Object object) {
        this.method = method;
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public Object getObject() {
        return object;
    }
}
