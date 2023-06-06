package com.cislc.shadow.manage.core.exception;

/**
 * @author szh
 * @ClassName BindCodeException
 * @Description 绑定标识符异常
 * @Date 2020/1/28 16:30
 **/
public class BindCodeExistException extends RuntimeException {

    public BindCodeExistException() {}

    public BindCodeExistException(String bindCode) {
        super("bind code: " + bindCode + "is exist.");
    }

}
