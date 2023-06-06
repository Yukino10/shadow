package com.shadow.web.service.auth;

import com.shadow.web.mapper.security.PermissionMapper;
import com.shadow.web.model.auth.RolePermission;
import com.shadow.web.model.auth.RolePermissionExample;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.security.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wangzhendong
 * @date: 2018年11月14日 下午2:03:09
 * @Copyright:
 */
@Service
@Slf4j
public class PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 通过userId 获取所属权限集
     * @param userId
     * @return
     */
    public Result<List<Permission>> findByUserId(Integer userId) {
        try {
            return Result.returnSuccess(permissionMapper.selectByUserId(userId));
        } catch (Exception e) {
            log.error("findByUserId failed:" + e);
            return Result.returnError("查询用户权限失败:" + e);
        }
    }

    /**
     * 通过userId 获取所属权限名称
     * @param userId
     * @return
     */
    public Result<List<String>> findNamesByUserId(Integer userId) {
        try {
            return Result.returnSuccess(permissionMapper.selectNamesByUserId(userId));
        }catch(Exception e) {
            log.error("findNamesByUserId failed:" + e);
            return Result.returnError("查询用户权限失败:" + e);
        }
    }

    /**
     * 判断userId是否含有permissionName的权限
     * @param userId
     * @param permissionName
     * @return 1:有 0:无
     */
    public Result<Integer> countByUserIdAndPermissionName(Integer userId, String permissionName) {
        try {
            int cnt = permissionMapper.selectCntByUserIdAndPermissionName(userId, permissionName);
            return Result.returnSuccess(cnt);
        }catch(Exception e) {
            log.error("countByUserIdAndPermissionName failed:" + e);
            return Result.returnError("查询用户权限失败:" + e);
        }
    }

    /**
     * 检查用户是否有对应的权限
     * @param userId
     * @param permissionName
     * @return
     */
    public Result<Boolean> checkHasPermission(Integer userId, String permissionName) {
        Result<Integer> ret = countByUserIdAndPermissionName(userId, permissionName);
        if(!ret.success()) {
            log.error("checkHasPermission failed:" + ret.msg());
            return Result.returnError("checkHasPermission failed:" + ret.msg());
        }
        return Result.returnSuccess(ret.value() < 1);
    }

}
