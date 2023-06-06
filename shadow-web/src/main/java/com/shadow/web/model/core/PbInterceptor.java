package com.shadow.web.model.core;

import com.shadow.web.model.result.ApiResult;
import com.shadow.web.utils.ReturnMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * 拦截返回Map的API请求，pb加密及日志处理
 * @author Administrator
 *
 */
@Component
public class PbInterceptor implements HandlerInterceptor {
	private static Logger log = LoggerFactory.getLogger(PbInterceptor.class);

	
	/**
	 * 返回Response的时候，输出异常堆栈信息，外层不再打印错误信息
	 * @param e
	 * @return
	 */
	private String exceptionStackTrack(Exception e) {
		StackTraceElement[] els = e.getStackTrace();
		StringBuilder sb = new StringBuilder();
		sb.append("Exception stackTrace:\n");
		for(StackTraceElement el : els) {
			sb.append(el.toString()).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * 把返回值信息经过ProtocalBuffer处理，输出到Response里面
	 * @param response
	 * @param info
	 */
	private void pbProcess(HttpServletResponse response, ApiResult.ApiResultInfo info) {
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();

			ReturnMessage.return_message.Builder builder = ReturnMessage.return_message.newBuilder();
			builder.setMsgCode(info.getMsgCode());
			String msg = info.getMsg();
			builder.setMsg(null != msg ? new String(msg.getBytes(), "utf-8") : null);
			String data = info.getData();
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
	
	/**
	 * 调用API之后，如果带有@RequestParam注解，继续把API的返回值信息添加到日志信息中，最后丢到队列中由其他线程插入到数据库
	 * 再就是返回值的ProtocalBuffer统一处理
	 */
	@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
		if(!(handler instanceof HandlerMethod)) {
			log.debug("not HandlerMethod, skip ");
			return;
		}
		/** 除非有特殊处理的API，API都要返回ApiResult对象，此时modelAndView非空*/
		if(null != modelAndView && !"/admin/index".equals(modelAndView.getViewName()) ) {
			Map<String, Object> model = modelAndView.getModel();
			ApiResult.ApiResultInfo info = (ApiResult.ApiResultInfo)model.get(ApiResult.resultAttr);
			if(null == info) {
				return;
			}
			/** 把返回值经过pb封装后，输出到response中*/
			pbProcess(response, info);	
			/** 填充API返回值信息*/
			//清理掉，不然会触发render
			modelAndView.clear();
		}
	}
}
