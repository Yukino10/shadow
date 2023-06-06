package com.shadow.web.service.authority;

import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.security.Permission;
import com.shadow.web.model.security.User;
import com.shadow.web.service.auth.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
	private static Logger log = LoggerFactory.getLogger(JwtUserDetailsServiceImpl.class);

    @Resource
    private PermissionService permissionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    JwtUserFactory jwtUserFactory;

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
    	/** step1: 根据用户名查用户*/
        Result<User> queryUserRet = userService.findByUserName(username);
        if(!queryUserRet.success()) {
        	log.error("loadUserByUsername failed: userService.getUserByName failed:" + queryUserRet.msg());
        	throw new UsernameNotFoundException("loadUserByUsername failed: queryUserByName failed:" + queryUserRet.msg());
        }
        User user = queryUserRet.value();
        /** step2: 根据用户查权限*/
        Result<List<Permission>> queryAuthorityRet = permissionService.findByUserId(user.getId());
        if(!queryAuthorityRet.success()) {
        	log.error("loadUserByUsername failed: authorityService.getAuthorities failed:" + queryAuthorityRet.msg());
        	throw new UsernameNotFoundException("loadUserByUsername failed: authorityService.getAuthorities failed:" + queryAuthorityRet.msg());
        }
        List<Permission> authorities = queryAuthorityRet.value();
        /** step3 : 创建JwtUser*/
        Result<JwtUser> createRet = jwtUserFactory.create(user, authorities);
        if(!createRet.success()) {
        	log.error("loadUserByUsername failed: createJWTUser failed:" + createRet.msg());
        	throw new UsernameNotFoundException("loadUserByUsername failed: createJWTUser failed:" + createRet.msg());
        }
        return createRet.value();
    }

}
