package com.communication.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: bin
 * @Date: 2019/10/23 15:51
 * @Description:
 *
 * 用于标注对外暴漏的方法，当有数据包到达时，可调用 value 值一致的方法。
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationService {
    String value();
}
