package com.shadow.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ReturnMsgUtil {
	private static Logger log = LoggerFactory.getLogger(ReturnMsgUtil.class);
	
	public static void doReturnSuccess(HttpServletResponse response, String data) {
		doReturn(response, 0, "", data);
	}
	
	private static String exceptionStackTrack(Exception e) {
		StackTraceElement[] els = e.getStackTrace();
		StringBuilder sb = new StringBuilder();
		sb.append("Exception stackTrace:\n");
		for(StackTraceElement el : els) {
			sb.append(el.toString()).append("\n");
		}
		return sb.toString();
	}
		
	public static void doReturn(HttpServletResponse response, int code, String msg, String data) {
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();

			ReturnMessage.return_message.Builder builder = ReturnMessage.return_message.newBuilder();
			builder.setMsgCode(code);
			builder.setMsg(null != msg ?new String(msg.getBytes(), "utf-8") : null);
			builder.setData(null != data ? new String(data.getBytes(), "utf-8") : null);
			ReturnMessage.return_message message = builder.build();
			message.writeTo(out);
		} catch (UnsupportedEncodingException e) {
			log.error("doReturn failed: UnsupportedEncodingException: " + e);
			log.error(exceptionStackTrack(e));
		} catch (IOException e) {
			log.error("doReturn failed: IOException: " + e);
			log.error(exceptionStackTrack(e));
		}
	}

}
