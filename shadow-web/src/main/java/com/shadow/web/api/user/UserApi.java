package com.shadow.web.api.user;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.result.UserInfo;
import com.shadow.web.model.security.User;
import com.shadow.web.service.authority.UserService;
import com.shadow.web.utils.ParamUtils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wangzhendong
 * @Date: 2019/11/26 16:29
 * @Description:
 */
@Controller
@RequestMapping("/admin/user")
@Slf4j
public class UserApi {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/info")
    public ApiResult getCurUserInfo() {
        Result<UserInfo> ret = userService.findCurUserInfo();
        if(!ret.success()) {
            log.error("getUserInfoForAvatar failed: {}",  ret.msg());
            return ApiResult.returnError(1,ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

    /*
    *     @ApiOperation(value="获取用户基础信息", notes="获取当前用户的基本信息")
    @GetMapping("/info")
    public ApiResult getCurUserInfo() {
        Result<UserInfo> ret = userInfoService.findCurUserInfo();
        if(!ret.success()) {
            log.error("getUserInfoForAvatar failed: {}",  ret.msg());
            return ApiResult.returnError(1,ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }
    * */
    /**
     * 按条件查询用户信息
     * @return userList
     * @auther 10413
     * @date 2021-04-10 14:23
     */
    //@Permission("am_companyMember_view")
    @PostMapping("searchUsers")
    public ApiResult searchUser(@RequestBody Map<String, Object> params) {
        ListParamValidResult<Map<String, Object>> validRet = userService.searchUsersParamValid(params);
        Result<PageInfo<UserInfo>> result = userService.searchUsersByClass(validRet);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess(ParamUtil.genReturnData(result.value()));
    }

    /**
     * @Description 重置用户密码
     * @Author szh
     * @Date 2018/12/16 22:24
     * @param userId 用户id
     * @return com.watermeter.framework.result.ApiResult
     **/
    //@Permission("am_companyMember_edit")
    @PostMapping("resetPwd/{userId}")
    public ApiResult resetPwd(@PathVariable int userId) {
        Result result = userService.resetPwd(userId);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * @return 批量删除用户
     * @auther 10413
     * @date 2021-04-10 16:48
     */
    //@Permission("am_companyMember_delete")
    @PostMapping("deleteUser")
    public ApiResult deleteUser(@RequestBody Integer[] userIdList) {
        Result result = userService.batchDeleteUser(userIdList);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * @return 获取角色穿梭框用户列表
     * @auther wangzhendong
     * @date 2018/12/9 15:10
     * 增加权限控制，
     */
    @GetMapping("/transfer")
    public ApiResult selectUser() {
        Result<List<User>> ret = userService.getUserSonUser();
        if (!ret.success()) {
            log.error("selectUser failed: {}" , ret.msg());
            return ApiResult.returnError(1, ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

    /**
     * @Description 校验登录名是否存在
     * @Author szh
     * @Date 2018/12/25 9:58
     * @param loginName 用户登录名
     * @return com.watermeter.framework.result.ApiResult
     **/
    @GetMapping("valid/{loginName}")
    public ApiResult validLoginName(@PathVariable String loginName) {
        Result result = userService.validLoginName(loginName);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess("");
    }
}
