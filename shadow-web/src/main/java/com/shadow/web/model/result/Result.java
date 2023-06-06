package com.shadow.web.model.result;

/**
 * @Auther: wangzhendong
 * @Date: 2019/10/22 16:09
 * @Description: 反参封装
 */
public class Result<T> {

	private String errMsg;  // error message	
	
	private T returnValue;  // return value when success
	
	public Result(String errMsg, T returnValue) {
		this.errMsg = errMsg;
		this.returnValue = returnValue;
	}
	
	public boolean success() {
		return null == errMsg;
	}
	
	public String msg() {
		return errMsg;
	}
	
	public T value() {
		return returnValue;
	}
	
	public static <E> Result<E> returnRet(String errMsg, E val) {
		Result<E> result = new Result<E>(errMsg, val);
		return result;
	}
	
	public static <E> Result<E> returnError(String errMsg) {
		return returnRet(errMsg, null);
	}
	
	public static <E> Result<E> returnSuccess() {
		return returnRet(null, null);
	}
	
	public static <E> Result<E> returnSuccess(E val) {
		return returnRet(null, val);
	}
}
