package com.shadow.web.service.authority;

import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.security.Permission;
import com.shadow.web.model.security.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public final class JwtUserFactory {
	private static Logger log = LoggerFactory.getLogger(JwtUserFactory.class);

	@Autowired
	UserService userService;
	
    private JwtUserFactory() {
    }
    
    /**
     *  获取用户密码
     * @param userId
     * @return
     */
    private Result<String> getUserPwd(Integer userId) {
    	return userService.findUserPwdByUserId(userId);
    }

    public Result<JwtUser> create(User user, List<Permission> authorities) {
    	Integer userId = user.getId();
    	Result<String> queryRet = getUserPwd(userId);
    	if(!queryRet.success()) {
    		log.error("create failed: {}" , queryRet.msg());
    		return Result.returnError("create JwtUser failed: query UserPwd failed:" + queryRet.msg());
    	}
    	String password = (String)queryRet.value();
        return Result.returnSuccess(new JwtUser(
        		userId,
                user.getUsername(),
                password,
                user.getDeleted(),
                mapToGrantedAuthorities(authorities),
                user.getCreateTime(),
                user.getLastLogoutTime()
        ));
    }

    private List<GrantedAuthority> mapToGrantedAuthorities(List<Permission> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getPermissionName()))
                .collect(Collectors.toList());
    }
}
