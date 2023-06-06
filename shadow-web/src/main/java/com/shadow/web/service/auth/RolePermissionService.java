package com.shadow.web.service.auth;

import com.shadow.web.mapper.auth.RolePermissionMapper;
import com.shadow.web.model.auth.Function;
import com.shadow.web.model.auth.RolePermission;
import com.shadow.web.model.auth.RolePermissionExample;
import com.shadow.web.model.result.Result;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/10 18:05
 * @Description:
 */
@Service
public class RolePermissionService {
    private static final Logger log = LoggerFactory.getLogger(RolePermissionService.class);

    @Autowired
    FunctionService functionNodeService;

    @Autowired
    PermissionFunctionService permissionFunctionService;

    @Resource
    RolePermissionMapper mapper;


    /**
     * 根据roleId获取已分配功能列表
     * @param roleId
     * @return
     */
    public Result<List<Function>> findRoleFunctionsByRoleId(Integer roleId) {
        return functionNodeService.findByRoleId(roleId);
    }

    private Result<List<Integer>> findPermissionIdsByFunctionIds(List<Integer> functionIds) {
        if(functionIds.isEmpty()) {
            return Result.returnSuccess(new ArrayList<Integer>());
        }
        return permissionFunctionService.findPermissionIdsByFunctionIds(functionIds);
    }

    private Result<List<Integer>> findPermissionIdsByRoleId(Integer roleId) {
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andDeletedEqualTo(0).andRoleIdEqualTo(roleId);
        Result<List<RolePermission>> findRet =  findByExample(example);
        if(!findRet.success()) {
            log.error("findPermissionIdsByRoleId failed:" + findRet.msg());
            return Result.returnError("findPermissionIdsByRoleId failed:" + findRet.msg());
        }
        List<RolePermission> list = findRet.value();
        if(null == list || list.isEmpty()) {
            return Result.returnSuccess(new ArrayList<Integer>());
        }
        return Result.returnSuccess(list.stream().map(item->item.getPermissionId()).collect(Collectors.toList()));
    }

    private Result<List<Integer>> findIdsByRoleIdAndPermissionIds(Integer roleId, List<Integer> permissionIds) {
        RolePermissionExample example = new RolePermissionExample();
        example.createCriteria().andDeletedEqualTo(0).andRoleIdEqualTo(roleId).andPermissionIdIn(permissionIds);
        Result<List<RolePermission>> findRet = findByExample(example);
        List<RolePermission> list = findRet.value();
        if(null == list || list.isEmpty()) {
            return Result.returnSuccess(new ArrayList<Integer>());
        }
        return Result.returnSuccess(list.stream().map(item->item.getId()).collect(Collectors.toList()));
    }

    private Result<Boolean> deleteByPermissionIds(Integer roleId, List<Integer> permissionIds) {
        /** step0: 为空则不需要任何处理，返回*/
        if(null == permissionIds || permissionIds.isEmpty()) {
            return Result.returnSuccess();
        }
        /** step1: 查询rolePermissionId*/
        Result<List<Integer>> findRet = findIdsByRoleIdAndPermissionIds(roleId, permissionIds);
        if(!findRet.success()) {
            log.error("deleteByPermissionIds failed:" + findRet.success());
            return Result.returnError("deleteByPermissionIds failed:" + findRet.success());
        }
        List<Integer> ids = findRet.value();
        /** step2: 执行删除*/
        for(Integer id : ids) {
            Result<Boolean> ret = delete(id);
            if(!ret.success()) {
                log.error("deleteByPermissionIds failed:" + ret.success());
                return Result.returnError("deleteByPermissionIds failed:" + ret.success());
            }
        }
        /** step3: 返回*/
        return Result.returnSuccess();
    }

    private Result<Boolean> createByPermissionIds(Integer roleId, List<Integer> permissionIds) {
        for(Integer permissionId : permissionIds) {
            RolePermission record = new RolePermission();
            record.setRoleId(roleId);
            record.setPermissionId(permissionId);
            Result<Integer> ret = create(record);
            if(!ret.success()) {
                log.error("createByPermissionIds failed:" + ret.success());
                return Result.returnError("createByPermissionIds failed:" + ret.success());
            }
        }
        return Result.returnSuccess();
    }

    @Transactional
    public Result<Boolean> updateRolePermission(Integer roleId, List<Integer> functionIds) {
        /** step1: 根据functionIds查询对应的PermissionId*/
        Result<List<Integer>> ret = findPermissionIdsByFunctionIds(functionIds);
        if(!ret.success()) {
            log.error("updateRolePermission failed: {}" , ret.msg());
            return Result.returnError("updateRolePermission failed:" + ret.msg());
        }
        List<Integer>permissionIds = ret.value();
        /** step2: 查询roleId对应的原有权限列表*/
        Result<List<Integer>> findPermissionRet = findPermissionIdsByRoleId(roleId);
        if(!findPermissionRet.success()) {
            log.error("updateRolePermission failed: {}" , findPermissionRet.msg());
            return Result.returnError("updateRolePermission failed:" + findPermissionRet.msg());
        }
        List<Integer> oldPermissionIds = findPermissionRet.value();
        /** step3: 删除对应的权限*/
        List<Integer> needDeleteIds = oldPermissionIds.stream().filter(item->!permissionIds.contains(item)).collect(Collectors.toList());
        Result<Boolean> deleteRet = deleteByPermissionIds(roleId, needDeleteIds);
        if(!deleteRet.success()) {
            log.error("updateRolePermission failed: {}" , deleteRet.msg());
            throw new PersistenceException("updateRolePermission failed:" + deleteRet.msg());
        }
        /** step4: 新增对应权限*/
        List<Integer> needCreateIds = permissionIds.stream().filter(item->!oldPermissionIds.contains(item)).collect(Collectors.toList());
        Result<Boolean> createRet = createByPermissionIds(roleId, needCreateIds);
        if(!createRet.success()) {
            log.error("updateRolePermission failed: {}" , createRet.msg());
            throw new PersistenceException("updateRolePermission failed:" + createRet.msg());
        }
        return Result.returnSuccess();
    }

    public Result<List<RolePermission>> findByExample(RolePermissionExample example) {
        try {
            return Result.returnSuccess(mapper.selectByExample(example));
        } catch (Exception e) {
            log.error("findByExample error:" + e);
            return Result.returnError("findByExample error:" + e);
        }
    }

    public Result<Boolean> delete(Integer id) {
        try {
            int ret = mapper.deleteByPrimaryKey(id);
            if (-1 == ret) {
                log.error("delete failed: delete return -1");
                return Result.returnError("delete failed: delete return -1");
            }
            return Result.returnSuccess();
        } catch (Exception e) {
            log.error("delete error:" + e);
            return Result.returnError("delete error:" + e);
        }
    }

    public Result<Integer> create(RolePermission record) {
        try {
            int ret = mapper.insertSelective(record);
            if (-1 == ret) {
                log.error("create failed: insert return -1");
                return Result.returnError("create failed: insert return -1");
            }
            return Result.returnSuccess(record.getId());
        } catch (Exception e) {
            log.error("create error:" + e);
            return Result.returnError("create error:" + e);
        }
    }
}
