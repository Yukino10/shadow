package com.cislc.shadow.manage.core.exception;
/**
 * @ClassName NoDeviceIdException
 * @Description 无设备id异常
 * @Date 2019/6/19 10:19
 * @author szh
 **/
public class NoDeviceIdException extends RuntimeException {

    public NoDeviceIdException() {
        super("entity's device id is null");
    }

}
