package com.shadow.web.service.auth;

import com.shadow.web.mapper.auth.FunctionMapper;
import com.shadow.web.model.auth.Function;
import com.shadow.web.model.auth.FunctionExample;
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
 * @Date: 2018/12/9 21:09
 * @Description:
 */
@Service
public class FunctionService {
    private static final Logger log = LoggerFactory.getLogger(FunctionService.class);

    @Resource
    FunctionMapper functionMapper;

    public Result<List<Integer>> findAllParentIds() {
        try {
            return Result.returnSuccess(functionMapper.selectAllParentIds());
        }catch(Exception e) {
            log.error("findAllParentIds error: {}" , e);
            return Result.returnError("findAllParentIds error:" + e);
        }
    }

    public Result<List<Function>> findByRoleId(Integer roleId) {
        try {
            return Result.returnSuccess(functionMapper.selectByRoleId(roleId));
        }catch(Exception e) {
            log.error("findByRoleId error: {}" , e);
            return Result.returnError("findByRoleId error:" + e);
        }
    }


    public Result<List<Function>> findByExample(FunctionExample example) {
        try {
            return Result.returnSuccess(functionMapper.selectByExample(example));
        }catch(Exception e) {
            log.error("findByExample error: {}" , e);
            return Result.returnError("findByExample error:" + e);
        }
    }

}
