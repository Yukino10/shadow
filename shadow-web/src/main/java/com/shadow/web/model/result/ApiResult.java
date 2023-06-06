package com.shadow.web.model.result;

import lombok.Data;

import java.util.HashMap;

/**
 * controller层api统一返回此类作为结果，后续在拦截器里面取数据
 * @author Administrator
 *
 */
public class ApiResult extends HashMap<String, Object>{

	private static final long serialVersionUID = 2572367218793892072L;

	public static String resultAttr = "ApiResultAttr";

	public ApiResult(Integer msgCode, String msg, String data) {
		this.put(resultAttr, new ApiResultInfo(msgCode, msg, data));
	}
	
	public static ApiResult returnSuccess(String data) {
		return new ApiResult(0, "", data);
	}

	public static ApiResult returnSuccess(){return new ApiResult(0,"","");}
	
	public static ApiResult returnError(Integer msgCode, String msg) {
		return new ApiResult(msgCode, msg, "");
	}
	
	@Data
	public static class ApiResultInfo{
		private Integer msgCode;
		private String msg;
		private String data;
		
		public ApiResultInfo(Integer msgCode, String msg, String data) {
			this.msgCode = msgCode;
			this.msg = msg;
			this.data = data;
		}
	}	
}
