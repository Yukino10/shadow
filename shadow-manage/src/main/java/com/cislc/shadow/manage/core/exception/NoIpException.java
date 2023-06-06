package com.cislc.shadow.manage.core.exception;
/**
 * @ClassName NoIpException
 * @Description 无ip异常
 * @Date 2019/11/25 17:17
 * @author szh
 **/
public class NoIpException extends RuntimeException {

    public NoIpException() {
        super("Communication cannot be completed, device's ip is null");
    }

}
