package com.shadow.web.api.authority;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.params.CreateUserParams;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.security.User;
import com.shadow.web.service.authority.JwtTokenUtil;
import com.shadow.web.service.auth.PermissionService;
import com.shadow.web.service.authority.RSAService;
import com.shadow.web.service.authority.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class AuthenticationApi {
    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.route.authentication.pre-auth}")
    private String superPermissionName;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Resource
    UserDetailsService userDetailsService;

    @Autowired
    UserService userService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RSAService rsaService;

    /**
     * @return 获取当前用户的ID
     * @auther wangzhendong
     * @date 2019/10/21 16:32
     */
    private Integer getCurUserId() {
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getId();
    }

    /**
     * @param request 访问服务器的请求
     * @return 登陆前向服务器获取RSA加密的公私钥
     * @auther wangzhendong
     * @date 2019/10/21 16:34
     */
    @PostMapping("${jwt.route.authentication.pre-auth}")
    public ApiResult preAuth(HttpServletRequest request) {
        HttpSession session = request.getSession();
        /** step1: 生成一对公私钥*/
        Result<KeyPair> ret = rsaService.generateKeyPair();
        if (!ret.success()) {
            return ApiResult.returnError(1, "preAuth failed:" + ret.msg());
        }
        KeyPair key = ret.value();
        /** step2: 私钥放到session里面*/
        RSAPrivateKey privateKey = (RSAPrivateKey) key.getPrivate();
        session.setAttribute(RSAService.PrivateKeySessionAttr, privateKey);
        /** step3: 公钥返回给前端*/
        RSAPublicKey publicKey = (RSAPublicKey) key.getPublic();
        Base64 base64 = new Base64();
        String encoded = base64.encodeToString(publicKey.getEncoded());
        return ApiResult.returnSuccess("{\"encoded\":\"" + encoded + "\"}");
    }

    /**
     * @param input   登陆的入参： username 用户名 不能为空
     *                password 密码  不能为空
     * @param request 访问服务器的请求
     * @return 登陆
     * @auther wangzhendong
     * @date 2018/12/21 16:50
     */
    @PostMapping("${jwt.route.authentication.path}")
    public ApiResult login(@RequestBody Map<String, String> input, HttpServletRequest request) {
        /** step1: 入参校验*/
        String username = input.get("username");
        String password = input.get("password");
        if (null == username || username.isEmpty()) {
            return ApiResult.returnError(1, "error:input param username is null");
        }
        /** step2: 解密加密后的密码*/
        Result<String> ret = rsaService.decryptPwd(request, password);
        if (!ret.success()) {
            return ApiResult.returnError(1, "密码解密失败:" + ret.msg());
        }
        String realPwd = ret.value();
        /** step3: 鉴权及返回token*/
        try {
            final JwtUser userDetails = (JwtUser) userDetailsService.loadUserByUsername(username);
            String token = jwtTokenUtil.generateToken(userDetails);
            /** step2: 获取用户对应的权限列表*/
            Result<List<String>> retauth = permissionService.findNamesByUserId(userDetails.getId());
            if (!ret.success()) {
                log.error("checkPermission failed when getUserAuthority:" + ret.msg());
                return ApiResult.returnError(1, "获取用户权限失败:" + ret.msg());
            }
            JSONObject data = new JSONObject();
            data.put("token", token);
            data.put("userId", userDetails.getId());
            data.put("currentAuthority", retauth.value()); //供前端判断权限 用，暂定用用户名，也可以改成ID
            /** step4: 清理掉session里面的私钥*/
            request.getSession().removeAttribute(RSAService.PrivateKeySessionAttr);

            final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, realPwd));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ApiResult.returnSuccess(data.toString());
        } catch (UsernameNotFoundException e) {
            log.error("login error: {}", e);
            return ApiResult.returnError(1, "该用户不存在，请重新填写或联系后台管理员");
        } catch (DisabledException e) {
            log.error("login error: {}", e);
            return ApiResult.returnError(1, "用户已失效");
        } catch (LockedException e) {
            log.error("login error: {}", e);
            return ApiResult.returnError(1, "用户已被锁定");
        } catch (BadCredentialsException e) {
            log.error("login error:", e);
            return ApiResult.returnError(1, "密码错误，请输入正确的密码");
        }
    }

    /**
     * @param request 用户访问服务器的请求
     * @return 用户登出
     * @auther wangzhendong
     * @date 2018/12/21 17:07
     */
    @PostMapping("${jwt.route.authentication.logout}")
    public ApiResult logout(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) (SecurityContextHolder
                .getContext().getAuthentication());
        JwtUser userDetails = (JwtUser) (authenticationToken.getPrincipal());

        User user = new User();
        user.setId(userDetails.getId());
        user.setLastLogoutTime(new Timestamp(System.currentTimeMillis()));
        //将token置为过期
        Result<Boolean> ret = userService.update(user);
        if (!ret.success()) {
            log.error("logout failed: update user lastLogoutTime failed:" + ret.msg());
            return ApiResult.returnError(1, "登出失败: 更新用户登出时间失败" + ret.msg());
        }
        //摧毁session
        HttpSession session = request.getSession();
        if (null != session) {
            session.invalidate();
        }

        //清空认证信息,security filter会自动将context清空
        //SecurityContextHolder.getContext().setAuthentication(null);
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("message", "退出成功");
        return ApiResult.returnSuccess(JSON.toJSONString(result));
    }

    //内部类 密码
    @Data
    private class ChangePasswordCtx {
        private Integer userId;
        private String newPwd;
    }

    /**
     * @return 修改密码 对于超级管理员来说不需要旧密码，其他用户需要旧密码
     * @param input 入参： oldPassword  newPassword 都是非空的
     * @param request 访问服务的请求
     * @auther wangzhendong
     * @date 2018/12/22 16:50
     */
    @PostMapping("${jwt.route.authentication.change-pwd}")
    public ApiResult changePassword(@RequestBody Map<String, Object> input, HttpServletRequest request) {
        /* step1 入参校验及处理 */
        Result<ChangePasswordCtx> preRet = preChangeOwnPwd(input, request);
        if (!preRet.success()) {
            log.error("changPassword failed:" + preRet.msg());
            return ApiResult.returnError(1, "修改密码失败:" + preRet.msg());
        }
        ChangePasswordCtx ctx = preRet.value();
        /* step2 修改用户密码 */
        //解密Rsa加密的新密码 密码传输保护 解密后为明文新密码
        Result<String> decryptRet = rsaService.decryptPwd(request, ctx.getNewPwd());
        if (!decryptRet.success()) {
            log.error("changePassword failed: " + decryptRet.msg());
            return ApiResult.returnError(1, "修改密码失败:" + decryptRet.msg());
        }
        String decryptNewPwd = decryptRet.value();
        //更新用户信息
        Result<Boolean> ret = userService.changeUserPwd(ctx.getUserId(), decryptNewPwd);
        if (!ret.success()) {
            log.error("changPassword failed:" + ret.msg());
            return ApiResult.returnError(1, "修改密码失败:" + ret.msg());
        }
        return ApiResult.returnSuccess("");
    }
    /**
     * @return: 密码比对
     * @auther: wangzhendong
     * @date: 2018/11/10 15:38
     */
    private Result<Boolean> checkUserPwd(Integer userId, String pwd, HttpServletRequest request) {
        /* step1: 密码解密 */
        Result<String> decryptRet = rsaService.decryptPwd(request, pwd);
        if (!decryptRet.success()) {
            log.error("checkUserPwd failed: " + decryptRet.msg());
            return Result.returnError("checkUserPwd failed: " + decryptRet.msg());
        }
        String password = decryptRet.value();
        /* step2: 检查密码是否匹配 */
        return userService.checkUserPwd(userId, password);
    }

    /**
     * @return 修改密码前的参数校验
     * @auther wangzhendong
     * @date 2018/12/22 16:49
     */
    private Result<ChangePasswordCtx> preChangeOwnPwd(Map<String, Object> input, HttpServletRequest request) {
        /* step1 入参非空校验 */
        String oldPwd = (String) input.get("oldPassword");
        String newPwd = (String) input.get("newPassword");
        if (StringUtils.isEmpty(oldPwd)) {
            log.error("preChangeOwnPwd failed: input param oldPassword is null or empty");
            return Result.returnError("input param oldPassword is null or empty");
        }
        if (StringUtils.isEmpty(newPwd)) {
            log.error("preChangeOwnPwd failed: input param newPassword is null or empty");
            return Result.returnError("input param newPassword is null or empty");
        }
        /* step2 获取当前用户ID */
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Integer userId = userDetails.getId();
        /* step3 旧密码校验 */
        Result<Boolean> checkRet = checkUserPwd(userId, oldPwd, request);
        if (!checkRet.success()) {
            log.error("preChangeOwnPwd failed:" + checkRet.msg());
            return Result.returnError("preChangeOwnPwd failed:" + checkRet.msg());
        }
        if (!checkRet.value()) {
            log.error("preChangeOwnPwd failed: input param oldPassword is not correct");
            return Result.returnError("用户密码不正确");
        }
        /* step4 返回结果 */
        ChangePasswordCtx ctx = new ChangePasswordCtx();
        ctx.setUserId(userId);
        ctx.setNewPwd(newPwd);
        return Result.returnSuccess(ctx);
    }

    /**
     * 权限校验
     * 检查当前用户是否有对应的权限
     *
     * @param input
     * @param response
     */
    @PostMapping(value = "/admin/checkPermission")
    public ApiResult checkPermission(@RequestBody Map<String, String> input, HttpServletResponse response) {
        String authority = input.get("authority");
        if (StringUtils.isEmpty(authority)) {
            return ApiResult.returnError(1, "input param authority is null");
        }
        /** step1: 获取当前用户ID*/
        Integer userId = getCurUserId();
        /** step2: 查询用户是否具有对应权限*/
        Result<Boolean> ret = permissionService.checkHasPermission(userId, authority);
        if (!ret.success()) {
            log.error("checkPermission failed:" + ret.msg());
            return ApiResult.returnError(1, "检查用户权限失败:" + ret.msg());
        }
        //暂时为了前端鉴权容易处理，没有权限的时候返回status=400
        if (!ret.value()) {
            response.setStatus(400);
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 供前端用来判断是否已经登录
     * 无需任何操作，因为如果token校验不通过直接返回401了
     * 但是非生产环境没有开启鉴权，所以加了些判断逻辑
     */
    @GetMapping(value = "/admin/checkLogin")
    public void checkLogin(HttpServletResponse response) {
        if (null != SecurityContextHolder.getContext().getAuthentication()) {
            return;
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
    }

    /**
     * 查询用户的所有权限用来判断哪些菜单需要渲染
     *
     * @param response
     */
    @PostMapping(value = "/admin/authorities")
    public ApiResult getPermission(HttpServletResponse response) {
        /** step1: 获取当前用户ID*/
        Integer userId = getCurUserId();
        /** step2: 获取用户对应的权限列表*/
        Result<List<String>> ret = permissionService.findNamesByUserId(userId);
        if (!ret.success()) {
            log.error("checkPermission failed when getUserAuthority:" + ret.msg());
            return ApiResult.returnError(1, "获取用户权限失败:" + ret.msg());
        }
        if (null == ret.value()) {
            return ApiResult.returnSuccess("[]");
        }
        return ApiResult.returnSuccess(JSON.toJSONString(ret.value()));
    }

    /**
     * @return 注册
     * @auther 10413
     * @date 2021-04-10 22:41
     */
    @PostMapping("/auth/register")
    public ApiResult register(@RequestBody Map<String, String> input, HttpServletRequest request){
        CreateUserParams params = new CreateUserParams();
        params.setUsername(input.get("username"));
        params.setPwd(input.get("password"));
        Result result = userService.createUser(params);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess();
    }

}
