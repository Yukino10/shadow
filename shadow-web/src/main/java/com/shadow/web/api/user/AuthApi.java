package com.shadow.web.api.user;

import com.alibaba.fastjson.JSONObject;
import com.shadow.web.model.params.Tree;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.auth.FunctionTreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Auther: 10413
 * @Date: 2021-04-11 21:17
 * @Description:
 */
@Controller
@RequestMapping("/admin/auth")
public class AuthApi {
    private static Logger log = LoggerFactory.getLogger(AuthApi.class);

    @Autowired
    FunctionTreeService treeService;

    /**
     * @return 获取菜单权限树
     * @auther 10413
     * @date 2021-04-11 21:18
     */
    @GetMapping("/tree")
    public ApiResult queryTreeNode(){
        Result<List<Tree>> ret = treeService.queryTreeNode();
        if(!ret.success()){
            log.error("queryTreeNode failed: {}",ret.msg());
            return ApiResult.returnError(1, "获取权限结构失败:" + ret.msg());
        }
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

}
