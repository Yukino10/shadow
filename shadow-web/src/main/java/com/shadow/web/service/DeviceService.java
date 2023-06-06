package com.shadow.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shadow.web.mapper.device.DeviceMapper;
import com.shadow.web.mapper.device.DevicePropChildMapper;
import com.shadow.web.mapper.device.DevicePropMapper;
import com.shadow.web.mapper.product.ProductMapper;
import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.device.*;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.product.Product;
import com.shadow.web.model.result.ProductInfo;
import com.shadow.web.model.result.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shadow.web.utils.ParamUtils.Constants.MSG_DATABASE_OPERATION_ERROR;
import static com.shadow.web.utils.ParamUtils.Constants.MSG_QUERY_FAILED;


/**
 * @Auther: 10413
 * @Date: 2020/9/27 14:34
 * @Description:
 */
@Service
public class DeviceService {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DevicePropMapper devicePropMapper;
    @Resource
    private DevicePropChildMapper devicePropChildMapper;
    @Resource
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;

    //查
    public Result<List<Device>> selectDevice() {
        DeviceExample example = new DeviceExample();
        return Result.returnSuccess(deviceMapper.selectByExample(example));
    }

    /**
     * 查询产品，参数校验，初始化分页参数
     * @auther 10413
     * @date 2021-04-22 14:24
     */
    public ListParamValidResult<DeviceExample> searchDeviceParamValid(Map<String, Object> input) {
        ListParamValidResult<DeviceExample> ret = new ListParamValidResult<>(input);
        DeviceExample example = new DeviceExample();
        DeviceExample.Criteria exampleRet = example.createCriteria().andDeletedEqualTo(0);
        // 当前登录用户的id
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Integer userId = userDetails.getId();
        String userName = userDetails.getUsername();
        if(!userName.equals("admin")){
            exampleRet.andUserIdEqualTo(userId);
        }
        ret.setExample(example);
        return ret;
    }

    /**
     * @return 查询产品列表
     * @auther 10413
     * @date 2021-04-10 14:31
     */
    public Result<PageInfo<Device>> searchDeviceByExample(ListParamValidResult<DeviceExample> validRet) {
        PageHelper.startPage(validRet.getPageNum(), validRet.getPageSize(), validRet.getOrderBy());
        try {
            List<Device> infoList = deviceMapper.selectByExample((DeviceExample)validRet.getExample());
            PageInfo<Device> pageInfo = new PageInfo<>(infoList);
            return Result.returnSuccess(pageInfo);
        } catch (Exception e) {
            return Result.returnError(MSG_QUERY_FAILED);
        }
    }

    //删
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDevice(Integer id) {
        int ret = deviceMapper.deleteByPrimaryKey(id);
        if (ret == -1) {
            return Result.returnError("删除产品失败");
        }
        return Result.returnSuccess();
    }

    //增
    @Transactional(rollbackFor = Exception.class)
    public Result insertDevice(Device device) {
        int ret = deviceMapper.insertSelective(device);
        if (ret == -1) {
            return Result.returnError("新增产品失败");
        }
        return Result.returnSuccess();
    }

    //查属性
    public Result<List<DeviceProp>> selectDeviceProp(int id) {
        DevicePropExample example = new DevicePropExample();
        example.createCriteria().andDeviceIdEqualTo(id).andDeletedEqualTo(0);
        return Result.returnSuccess(devicePropMapper.selectByExample(example));
    }

    /**
     * 查子属性
     * @param propId 属性id
     * @return 子属性
     */
    public Result<List<DevicePropChild>> selectDevicePropChild(int propId) {
        DevicePropChildExample example = new DevicePropChildExample();
        example.createCriteria().andPropIdEqualTo(propId).andDeletedEqualTo(0);
        return Result.returnSuccess(devicePropChildMapper.selectByExample(example));
    }

    //删属性
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDeviceProp(Integer id) {
        List<Product> productList = productMapper.selectByDevicePropertyId(id);
        if (CollectionUtils.isEmpty(productList)) {
            productService.setProductUpdated(productList);
        }

        int ret = devicePropMapper.deleteByPrimaryKey(id);
        if (ret == -1) {
            return Result.returnError("删除产品失败");
        }
        return Result.returnSuccess();
    }
    //增属性
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> insertDeviceProp(DeviceProp prop) {
        Result<List<Product>> re = productService.selectByDeviceId(prop.getDeviceId());
        List<Product> productList = re.value();
        if (CollectionUtils.isEmpty(productList)) {
            productService.setProductUpdated(productList);
        }

        int ret = devicePropMapper.insertSelective(prop);
        if (ret == -1) {
            return Result.returnError("新增物模型属性失败");
        }
        return Result.returnSuccess(prop.getId());
    }

    //增
    public Result insertDevicePropChild(DevicePropChild prop){
        List<Product> productList = productMapper.selectByDevicePropertyId(prop.getPropId());
        if (CollectionUtils.isEmpty(productList)) {
            productService.setProductUpdated(productList);
        }

        int ret = devicePropChildMapper.insertSelective(prop);
        if (ret == -1) {
            return Result.returnError("新增物模型属性子类失败");
        }
        return Result.returnSuccess();
    }

}
