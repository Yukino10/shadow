package com.shadow.web.api.user;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shadow.web.model.auth.Role;
import com.shadow.web.model.auth.RoleExample;
import com.shadow.web.model.auth.UserRole;
import com.shadow.web.model.core.Permission;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.params.RoleParams;
import com.shadow.web.model.params.WhenUpdateIdNotNull;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.auth.CommonService;
import com.shadow.web.service.auth.RoleService;
import com.shadow.web.utils.ParamUtils.ParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static com.shadow.web.utils.ParamUtils.ParamUtil.*;

/**
 * @Auther: 10413
 * @Date: 2021-04-10 18:19
 * @Description:
 */
@Controller
@RequestMapping("/admin/role")
public class RoleApi {
    private static final Logger log = LoggerFactory.getLogger(RoleApi.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private CommonService commonService;

    /**
     * @param input 查询条件
     * @return 获取角色列表
     * @auther wangzhendong
     * @date 2018/12/7 15:11
     */
    //@Permission("am_securityRole_view")
    @PostMapping("/list")
    public ApiResult findRoleList(@RequestBody Map<String, Object> input) {
        ListParamValidResult<RoleExample> validResult = roleService.findRoleListParamValid(input);
        if (!validResult.isSuccess()) {
            return ApiResult.returnError(2, "获取角色列表失败:" + validResult.getErrMsg());
        }
        Result<PageInfo<Role>> result = roleService.findRoleList(validResult);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        PageInfo<Role> roles = result.value();
        return ApiResult.returnSuccess(ParamUtil.genReturnData(roles));
    }

    /**
     * @param param 角色属性
     * @return 新建角色
     * @auther wangzhendong
     * @date 2018/12/7 15:26
     */
    //@Permission("am_securityRole_create")
    @PutMapping
    public ApiResult createRole(@RequestBody @Validated RoleParams param) {
        Result<Integer> ret = roleService.create(param.getRole());
        if (!ret.success()) {
            return ApiResult.returnError(1, "createRole failed:" + ret.msg());
        }
        return ApiResult.returnSuccess("");
    }


    /**
     * @param roleName 角色名称
     * @return 校验角色名称是否重复
     * @auther wangzhendong
     * @date 2018/12/7 15:12
     */
    @GetMapping("/name/{roleName}")
    public ApiResult checkRoleNameUnique(@PathVariable String roleName) {
        Result<Boolean> ret = roleService.checkRoleNameUnique(roleName);
        if (!ret.success()) {
            return ApiResult.returnError(1, ret.msg());
        }
        if (ret.value()) {
            return ApiResult.returnSuccess("");
        } else {
            return ApiResult.returnError(1, "角色名称已存在");
        }
    }

    /**
     * @param ids 要删除的角色id列表
     * @return 删除角色
     * @auther wangzhendong
     * @date 2018/12/7 15:44
     */
    //@Permission("am_securityRole_delete")
    @DeleteMapping("/{ids}")
    public ApiResult deleteRole(@PathVariable @NotEmpty(message = "角色ID不能为空") String ids) {
        List<Integer> idList = idsStrToList(ids);
        /* 校验删除数据是否存在外键关联数据 */
        for (Integer id : idList) {
            Result<String> result = commonService.verifyRelationData("cm_role", id);
            if (!result.success()) {
                log.error("deleteRole failed: {}", result.msg());
                return ApiResult.returnError(2, "删除前检查失败： 数据库错误:" + result.msg());
            }
            if (null != result.value()) {
                return ApiResult.returnError(2, getDeleteMsg(result.value()));
            }
        }
        /* 删除角色*/
        try {
            Result<Boolean> result = roleService.deleteRole(idList);
            if (!result.success()) {
                return ApiResult.returnError(1, "删除角色失败");
            }
            return ApiResult.returnSuccess("");
        } catch (Exception e) {
            log.error("deleteRole error: {}", e);
            return ApiResult.returnError(2, "deleteRole error:" + e);
        }
    }

    /**
     * @return 更新角色
     * @auther wangzhendong
     * @date 2018/12/7 17:53
     */
    //@Permission("am_securityRole_edit")
    @PatchMapping
    public ApiResult updateRole(@RequestBody @Validated(WhenUpdateIdNotNull.class) RoleParams param) {
        Result<Boolean> ret = roleService.update(param.getRole());
        if (!ret.success()) {
            return ApiResult.returnError(1, "updateRole failed:" + ret.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * @return 设置角色的人员
     * @auther wangzhendong
     * @date 2018/12/21 16:02
     */
    //@Permission("am_securityRole_setMember")
    @PatchMapping("/changeMember")
    public ApiResult changeMembers(@RequestBody Map<String, Object> input) {
        Integer roleId = (Integer) input.get("roleId");
        List<Integer> userIds = (List<Integer>) input.get("users");
        if (roleId == null || userIds == null || userIds.isEmpty()) {
            log.error("参数传递错误; ");
            return ApiResult.returnError(2, "roleID 不能为空");
        }
        try {
            Result<Boolean> ret = roleService.updateRoleUser(roleId, userIds);
            if (!ret.success()) {
                return ApiResult.returnError(1, "updateRole failed:" + ret.msg());
            }
        } catch (Exception e) {
            log.error("设置人员出错: {}", e.getMessage());
            return ApiResult.returnError(1, "设置人员出错: " + e);
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * @return 获取RoleUser对象信息
     * @auther wangzhendong
     * @date 2018/12/9 15:35
     */
    @PostMapping("/userRole/{roleId}")
    public ApiResult getUserRoleByRoleId(@PathVariable @NotNull(message = "角色ID不能为空") Integer roleId) {
        Result<List<UserRole>> ret = roleService.findUserRoleByRoleId(roleId);
        if (!ret.success()) {
            log.error("getUserRoleByRoleId failed: {}", ret.msg());
            return ApiResult.returnError(1, ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

    /**
     * @Description 获取角色下拉框信息
     * @Author szh
     * @Date 2018/12/14 21:39
     * @param
     * @return com.watermeter.framework.result.ApiResult
     **/
    @GetMapping("getRoleSelectList")
    public ApiResult getRoleSelectList() {
        Result<List<RoleParams>> ret = roleService.getRoleSelectList();
        if (!ret.success()) {
            return ApiResult.returnError(1, ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

    /**
     * 删除前外键关联校验信息提示
     */
    private String getDeleteMsg(String tableName) {
        String message;
        switch (tableName) {
            case "cm_user_role":
                message = "删除的角色已关联人员，无法删除";
                break;
            case "cm_role_permission":
                message = "删除的角色已分配权限，无法删除";
                break;
            default:
                message = tableName + ",服务器异常";
                break;
        }
        return message;
    }
}
