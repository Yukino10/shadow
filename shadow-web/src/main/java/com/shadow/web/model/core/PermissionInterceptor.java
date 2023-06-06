package com.shadow.web.model.core;

import com.shadow.web.utils.ReturnMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@Component
@Order(1)
public class PermissionInterceptor implements HandlerInterceptor {
	private static Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

	@Value("${jwt.header}")
    private String tokenHeader;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(!(handler instanceof HandlerMethod)) {
			log.debug("not HandlerMethod, skip ");
			return true;
		}
		/** step1: 获取权限名称*/
		HandlerMethod method = (HandlerMethod)handler;
		Permission permission = method.getMethod().getAnnotation(Permission.class);
		if(null == permission) {
			return true;
		}
		String permissionName = permission.value();
		/** step2: 从securityContext中获取用户权限*/
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
		        .getAuthentication()
		        .getPrincipal();
		Collection<? extends GrantedAuthority>  authorities = userDetails.getAuthorities();
		/** step3: 判断用户是否拥有对应权限*/
		for(GrantedAuthority authority: authorities) {
			if(permissionName.equals(authority.getAuthority())) {
				return true;
			}
		}
        log.error("request[" + request.getRequestURI() + "] preHandle failed: user[userName" + userDetails.getUsername() + "] do not have permission[" + permissionName + "]" );
        ReturnMsgUtil.doReturn(response, 2, "用户" + userDetails.getUsername() + "没有权限访问" + request.getRequestURI(), "");
        return false;
	}

}
