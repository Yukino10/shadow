package com.shadow.web.api.user;

import com.alibaba.fastjson.JSONObject;
import com.shadow.web.model.auth.Function;
import com.shadow.web.model.params.RolePermissionInsertParam;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.auth.FunctionService;
import com.shadow.web.service.auth.RolePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/10 18:03
 * @Description:
 */
@Controller
@RequestMapping("/admin/rolePermission")
public class RolePermissionApi {
    private static final Logger log = LoggerFactory.getLogger(RolePermissionApi.class);

    @Autowired
    RolePermissionService rolePermissionService;

    @Autowired
    FunctionService functionService;

    /**
     * @return 根据Role ID获取已分配的Function
     * @auther wangzhendong
     * @date 2018/12/10 18:10
     */
    //@Permission("am_securityPermission_view")
    @PostMapping("function/{roleId}")
    public ApiResult getFunctions(@PathVariable Integer roleId) {
        Result<List<Function>> ret = functionService.findByRoleId(roleId);
        if (!ret.success()) {
            log.error("getFunctions failed: {}" , ret.msg());
            return ApiResult.returnError(1, ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

    /**
     * @return 分配权限，创建Role与Permission关联关系
     * @auther wangzhendong
     * @date 2018/12/10 18:32
     */
    //@Permission("am_securityPermission_operate")
    @PutMapping
    public ApiResult updateRolePermission(@RequestBody @Validated RolePermissionInsertParam param) {
        if (null == param.getFunctions()) {
            param.setFunctions(new ArrayList<>());
        }
        try {
            Result<Boolean> ret = rolePermissionService.updateRolePermission(param.getRoleId(), param.getFunctions());
            if (!ret.success()) {
                log.error("updateRolePermission error: {}" , ret.msg());
                return ApiResult.returnError(1, "更新角色权限失败:" + ret.msg());
            }
        }catch(Exception e) {
            log.error("updateRolePermission error: {}" , e);
            return ApiResult.returnError(2, "updateRolePermission error:" + e);
        }

        return ApiResult.returnSuccess("");
    }

}
