package com.shadow.web.api.product;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shadow.web.model.device.Device;
import com.shadow.web.model.device.DeviceExample;
import com.shadow.web.model.device.DeviceProp;
import com.shadow.web.model.device.DevicePropChild;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.DeviceService;
import com.shadow.web.utils.ParamUtils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;

/**
 * @author 10413
 * @since 2020/9/27 10:41
 */
@Controller
@Slf4j
@RequestMapping("/admin/device")
public class DevicesApi {

    @Autowired
    DeviceService deviceService;

    /**
     * 新增物理模型定义
     * @return
     */
    @PostMapping("create")
    public ApiResult createDevice(@RequestBody Device device ){
        Result<?> ret = deviceService.insertDevice(device);
        if (!ret.success()){
            log.error(ret.msg());
            return ApiResult.returnError(1,"物模型数据库错误");
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 获取物模型列表
     * @return
     */
//    public ApiResult selectList(){
//        Result<List<Device>> ret = deviceService.selectDevice();
//        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
//    }
    @PostMapping("list")
    public ApiResult selectList(@RequestBody Map<String, Object> params) {
        ListParamValidResult<DeviceExample> validRet = deviceService.searchDeviceParamValid(params);
        Result<PageInfo<Device>> result = deviceService.searchDeviceByExample(validRet);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess(ParamUtil.genReturnData(result.value()));
    }


    /**
     * 删除产品信息
     * @param id
     * @return
     */
    @PostMapping("delete")
    public ApiResult deleteDevice(@RequestBody int id){
        Result<?> ret = deviceService.deleteDevice(id);
        if (!ret.success()){
            log.error(ret.msg());
            return ApiResult.returnError(1,ret.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 获取物模型属性列表
     * @return
     */
    @PostMapping("/prop/list")
    public ApiResult selectPropList(@RequestBody int id){
        Result<List<DeviceProp>> ret = deviceService.selectDeviceProp(id);
        return ApiResult.returnSuccess(JSONObject.toJSONString(ret.value()));
    }

    /**
     * 获取子属性列表
     *
     * @param propId 属性id
     * @return 子属性
     * @author szh
     * @since 2021/3/18 19:08
     */
    @PostMapping("/prop/child")
    public ApiResult selectChildPropList(@RequestBody int propId) {
        Result<List<DevicePropChild>> res = deviceService.selectDevicePropChild(propId);
        return ApiResult.returnSuccess(JSONObject.toJSONString(res.value()));
    }

    /**
     * 删除属性信息
     * @param id
     * @return
     */
    @PostMapping("/prop/delete")
    public ApiResult deleteDeviceProp(@RequestBody int id){
        Result<?> ret = deviceService.deleteDeviceProp(id);
        if (!ret.success()){
            log.error(ret.msg());
            return ApiResult.returnError(1,ret.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 新增物理模型定义
     * @return
     */
    @PostMapping("/prop/create")
    @Transactional(rollbackFor = Exception.class)
    public ApiResult createDeviceProp(@RequestBody Map<String,Object> map ){
        String type = (String)map.get("type");
        String name = (String)map.get("name");
        String structName = (String)map.get("structName");
        String construction = (String)map.get("construction");
        String description = (String)map.get("description");
        Integer deviceId = (Integer)map.get("deviceId");
        List<Map<String,Object>> propList = (List<Map<String,Object>>)map.get("propChildList");
        DeviceProp prop = new DeviceProp();
        prop.setConstruction(construction);
        prop.setName(name);
        prop.setStructName(structName);
        prop.setDescription(description);
        prop.setDeviceId(deviceId);
        Result<Integer> ret = deviceService.insertDeviceProp(prop);
        if (!ret.success()){
            log.error(ret.msg());
            return ApiResult.returnError(1,"物模型数据库错误");
        }
        if (CollectionUtils.isNotEmpty(propList)) {
            for (Map<String, Object> childMap : propList) {
                DevicePropChild child = new DevicePropChild();
                child.setPropId(ret.value());
                child.setName((String) childMap.get("propChildName"));
                child.setConstruction((String) childMap.get("construction"));
                child.setTag((String) childMap.get("propChildTag"));
                Result<?> propRet = deviceService.insertDevicePropChild(child);
                if (!propRet.success()) {
                    log.error(ret.msg());
                    return ApiResult.returnError(1, "物模型数据库错误");
                }
            }
        }
        return ApiResult.returnSuccess("");
    }

}
