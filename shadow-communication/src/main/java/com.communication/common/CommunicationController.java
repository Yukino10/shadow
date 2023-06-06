package com.communication.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: bin
 * @Date: 2019/10/23 15:41
 * @Description:
 *
 * 标注包含有 {@link CommunicationService} 方法的类，用于 {@link CommunicationScanner} 在启动时扫描。
 *
 * 标注此注解的类并不是单例
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationController {

}
