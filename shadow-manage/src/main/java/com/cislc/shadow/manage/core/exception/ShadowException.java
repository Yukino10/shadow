package com.cislc.shadow.manage.core.exception;

/**
 * 影子异常
 *
 * @author szh
 * @since 2021/5/11 15:13
 **/
public class ShadowException extends RuntimeException {

    public ShadowException() {}

    public ShadowException(String msg) {
        super("shadow exception: " + msg);
    }

}
