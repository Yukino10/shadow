package com.shadow.web.api.product;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shadow.web.model.base.BaseService;
import com.shadow.web.model.device.Device;
import com.shadow.web.model.device.DeviceExample;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.BaseServiceService;
import com.shadow.web.utils.ParamUtils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 10413
 * @Date: 2021-05-05 16:29
 * @Description:
 */
@Controller
@Slf4j
@RequestMapping("/admin/base")
public class BaseApi {

    @Autowired
    BaseServiceService baseService;

    /**
     * 获取物模型列表
     * @return
     */
    @PostMapping("list")
    public ApiResult selectList(){
        Result<List<BaseService>> ret = baseService.queryList();
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }
}
