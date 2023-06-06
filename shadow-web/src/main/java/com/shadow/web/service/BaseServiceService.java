package com.shadow.web.service;

import com.shadow.web.mapper.base.BaseServiceMapper;
import com.shadow.web.model.base.BaseService;
import com.shadow.web.model.base.BaseServiceExample;
import com.shadow.web.model.device.Device;
import com.shadow.web.model.device.DeviceExample;
import com.shadow.web.model.result.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: 10413
 * @Date: 2021-05-05 16:32
 * @Description:
 */
@Service
public class BaseServiceService {

    @Resource
    private BaseServiceMapper mapper;

    //查
    public Result<List<BaseService>> queryList() {
        BaseServiceExample example = new BaseServiceExample();
        return Result.returnSuccess(mapper.selectByExample(example));
    }
}
