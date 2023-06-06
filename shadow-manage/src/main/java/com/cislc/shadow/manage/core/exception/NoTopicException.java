package com.cislc.shadow.manage.core.exception;
/**
 * @ClassName NoTopicException
 * @Description 无mqtt主题异常
 * @Date 2019/12/23 22:06
 * @author szh
 **/
public class NoTopicException extends RuntimeException {

    public NoTopicException() {
        super("device's topic is null");
    }

}
