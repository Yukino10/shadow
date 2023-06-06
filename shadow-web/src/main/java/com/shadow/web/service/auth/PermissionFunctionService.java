package com.shadow.web.service.auth;

import com.shadow.web.mapper.auth.PermissionFunctionMapper;
import com.shadow.web.model.auth.PermissionFunction;
import com.shadow.web.model.auth.PermissionFunctionExample;
import com.shadow.web.model.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/10 18:37
 * @Description:
 */
@Service
public class PermissionFunctionService {
    private static final Logger log = LoggerFactory.getLogger(PermissionFunctionService.class);

    @Resource
    PermissionFunctionMapper mapper;

    public Result<List<Integer>> findPermissionIdsByFunctionIds(List<Integer> functionIds) {
        PermissionFunctionExample example = new PermissionFunctionExample();
        example.createCriteria().andDeletedEqualTo(0).andFunctionIdIn(functionIds);
        Result<List<PermissionFunction>> ret = findByExample(example);
        if(!ret.success()) {
            log.error("findPermissionIdsByFunctionIds failed: {}" , ret.msg());
            return Result.returnError("findPermissionIdsByFunctionIds failed:" + ret.msg());
        }
        List<PermissionFunction> list = ret.value();
        if(null != list && !list.isEmpty()) {
            return Result.returnSuccess(list.stream().map(item->item.getPermissionId()).collect(Collectors.toList()));
        }
        return Result.returnSuccess(new ArrayList<Integer>());
    }

    public Result<List<PermissionFunction>> findByExample(PermissionFunctionExample example) {
        try {
            return Result.returnSuccess(mapper.selectByExample(example));
        }catch(Exception e) {
            log.error("findByExample error:" + e);
            return Result.returnError("findByExample error:" + e);
        }
    }

}
