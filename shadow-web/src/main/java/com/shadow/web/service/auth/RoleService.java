package com.shadow.web.service.auth;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.shadow.web.mapper.auth.AdminRoleMapper;
import com.shadow.web.mapper.auth.RoleMapper;
import com.shadow.web.mapper.auth.UserRoleMapper;
import com.shadow.web.model.auth.Role;
import com.shadow.web.model.auth.RoleExample;
import com.shadow.web.model.auth.UserRole;
import com.shadow.web.model.auth.UserRoleExample;
import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.params.RoleParams;
import com.shadow.web.model.result.Result;
import com.shadow.web.utils.ParamUtils.SqlUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shadow.web.utils.ParamUtils.Constants.*;

/**
 * @Auther: 10413
 * @Date: 2021-04-10 19:35
 * @Description:
 */
@Service
public class RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    @Resource
    RoleMapper mapper;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private AdminRoleMapper adminRoleMapper;

    /**
     * @return 获取角色列表前的参数校验
     * @auther wangzhendong
     * @date 2018/12/6 10:37
     */
    public ListParamValidResult<RoleExample> findRoleListParamValid(Map<String,Object> input){
        ListParamValidResult<RoleExample> validRet = new ListParamValidResult<>(input);
        RoleExample example = new RoleExample();
        RoleExample.Criteria criteria = example.createCriteria().andDeletedEqualTo(0);
        String roleName = (String) input.get("roleName");
        if (StringUtil.isNotEmpty(roleName)) {
            criteria.andRoleNameLike(SqlUtils.wrapLike(roleName));
        }
        validRet.setExample(example);
        return validRet;
    }

    /**
     * @return 获取角色列表
     * @auther wangzhendong
     * @date 2018/12/6 10:46
     */
    public Result<PageInfo<Role>> findRoleList(ListParamValidResult<RoleExample> validRet) {
        PageHelper.startPage(validRet.getPageNum(), validRet.getPageSize());
        Result<List<Role>> findRet = findByExample(validRet.getExample());
        if(!findRet.success()) {
            log.error("findRoleList failed: {}" , findRet.msg());
            return Result.returnError("findRoleList failed:" + findRet.msg());
        }
        List<Role> list = findRet.value();
        return Result.returnSuccess(new PageInfo<>(null == list ? new ArrayList<Role>() : list));
    }


    /**
     * @return 校验角色名称是否存在
     * @auther wangzhendong
     * @date 2018/12/7 15:14
     */
    public Result<Boolean> checkRoleNameUnique(String name) {
        RoleExample example = new RoleExample();
        example.createCriteria().andDeletedEqualTo(0).andRoleNameEqualTo(name);
        Result<Integer> cntRet = countByExample(example);
        if(!cntRet.success()) {
            log.error("checkRoleNameUnique failed: {}" , cntRet.msg());
            return Result.returnError("checkRoleNameUnique failed:" + cntRet.msg());
        }
        return Result.returnSuccess(cntRet.value() < 1);
    }

    /**
     * @return 批量删除角色
     * @auther wangzhendong
     * @date 2018/12/7 16:27
     */
    @Transactional
    public Result<Boolean> deleteRole(List<Integer> ids) {
        for(Integer id : ids) {
            Result<Boolean> ret = delete(id);
            if(!ret.success()) {
                log.error("delete failed:" + ret.msg());
                throw new PersistenceException("delete failed:" + ret.msg());
            }
        }
        return Result.returnSuccess();
    }

    /**
     * @return 设置Role的人员
     * @auther wangzhendong
     * @date 2018/12/9 18:31
     */
    @Transactional
    public Result<Boolean> updateRoleUser(Integer roleId,List<Integer> userIds){
        /** step1: 查询老的userRole*/
        Result<List<UserRole>> findRet = findUserRoleByRoleId(roleId);
        if(!findRet.success()) {
            log.error("update failed: {}" , findRet.msg());
            return Result.returnError("update failed:" + findRet.msg());
        }
        List<UserRole> oldUserRoles = findRet.value();
        /** step3: 删除要删除的*/
        Result<Boolean> deleteRet = deleteOldUserRole(oldUserRoles, userIds);
        if(!deleteRet.success()) {
            log.error("update failed: {}" , deleteRet.msg());
            throw new PersistenceException("update failed:" + deleteRet.msg());
        }
        /** step4: 新增要新增的*/
        Result<Boolean> createRet = createNewUserRole(roleId, oldUserRoles, userIds);
        if(!createRet.success()) {
            log.error("update failed:" + createRet.msg());
            throw new PersistenceException("update failed:" + createRet.msg());
        }
        return Result.returnSuccess();
    }

    /**
     * @return 获取RoleId = roleId 的UserRole信息
     * @auther wangzhendong
     * @date 2018/12/9 15:37
     */
    public Result<List<UserRole>> findUserRoleByRoleId(Integer roleId) {
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andDeletedEqualTo(0).andRoleIdEqualTo(roleId);
        return userRoleService.findByExample(example);
    }

    /**
     * @return 删除要要删除的UserRole
     * @auther wangzhendong
     * @date 2018/12/9 18:29
     */
    private Result<Boolean> deleteOldUserRole(List<UserRole> oldUserRoles, List<Integer> userIds) {
        /** step1: 老的UserRole为空则直接返回*/
        if(null == oldUserRoles || oldUserRoles.isEmpty()) {
            return Result.returnSuccess();
        }
        /** step2: 找出要删除的UserRole*/
        List<UserRole> toDelete = null;
        if(null == userIds || userIds.isEmpty()) {
            toDelete = oldUserRoles;
        }else {
            toDelete = oldUserRoles.stream().filter(item->!userIds.contains(item.getUserId())).collect(Collectors.toList());
        }
        /** step3: 执行删除*/
        for(UserRole record: toDelete) {
            Result<Boolean> ret = userRoleService.delete(record.getId());
            if(!ret.success()) {
                log.error("deleteOldUserRole failed: {}" , ret.msg());
                return Result.returnError("deleteOldUserRole failed:" + ret.msg());
            }
        }
        /** step4: 返回*/
        return Result.returnSuccess();
    }

    /**
     * @return 增加要增加的UserRole
     * @auther wangzhendong
     * @date 2018/12/9 18:30
     */
    private Result<Boolean> createNewUserRole(Integer roleId, List<UserRole> oldUserRoles, List<Integer> userIds) {
        /** step1: 新的的userIds为空则直接返回*/
        if(null == userIds || userIds.isEmpty()) {
            return Result.returnSuccess();
        }
        /** step2: 找出要新增的User*/
        List<Integer> toCreate = null;
        if(null == oldUserRoles || oldUserRoles.isEmpty()) {
            toCreate = userIds;
        }else {
            List<Integer> oldUserIds = oldUserRoles.stream().map(UserRole::getUserId).collect(Collectors.toList());
            toCreate = userIds.stream().filter(item->!oldUserIds.contains(item)).collect(Collectors.toList());
        }
        /** step3: 执行创建*/
        for(Integer userId: toCreate) {
            UserRole record = new UserRole();
            record.setUserId(userId);
            record.setRoleId(roleId);
            Result<Integer> ret = userRoleService.create(record);
            if(!ret.success()) {
                log.error("createNewUserRole failed: {}" , ret.msg());
                return Result.returnError("createNewUserRole failed:" + ret.msg());
            }
        }
        /** step4: 返回*/
        return Result.returnSuccess();
    }

    /**
     * @Description 根据登录员工获取可查看的角色
     * @Author szh
     * @Date 2018/12/14 21:39
     **/
    public Result<List<RoleParams>> getRoleSelectList() {
        try {
            // 获取当前用户ID
            JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            Integer userId = userDetails.getId();
            List<RoleParams> result = adminRoleMapper.selectRoleByClass(userId);
            return Result.returnSuccess(result);
        } catch (Exception e) {
            log.error(MSG_DATABASE_OPERATION_ERROR + "\n" + e.toString());
            return Result.returnError(MSG_QUERY_FAILED + e.getMessage());
        }
    }


    //查询角色列表
    public Result<List<Role>> findByExample(RoleExample example) {
        try {
            return Result.returnSuccess(mapper.selectByExample(example));
        }catch(Exception e) {
            log.error("findByExample error: {}" , e);
            return Result.returnError("findByExample error:" + e);
        }
    }

    //创建角色
    public Result<Integer> create(Role record) {
        try {
            int ret = mapper.insertSelective(record);
            if(-1 == ret) {
                log.error("create failed: insert return -1");
                return Result.returnError("create failed: insert return -1");
            }
            return Result.returnSuccess(record.getId());
        }catch(Exception e) {
            log.error("create error: {}" , e);
            return Result.returnError("create error:" + e);
        }
    }

    //统计角色个数
    public Result<Integer> countByExample(RoleExample example){
        try {
            return Result.returnSuccess(mapper.countByExample(example));
        }catch(Exception e) {
            log.error("countByExample error: {}" , e);
            return Result.returnError("countByExample error:" + e);
        }
    }

    //删除角色
    public Result<Boolean> delete(Integer id) {
        try {
            int ret = mapper.deleteByPrimaryKey(id);
            if(-1 == ret) {
                log.error("delete failed: delete return -1");
                return Result.returnError("delete failed: delete return -1");
            }
            return Result.returnSuccess();
        }catch(Exception e) {
            log.error("delete error: {}" , e);
            return Result.returnError("delete error:" + e);
        }
    }

    //更新角色
    public Result<Boolean> update(Role record) {
        try {
            int ret = mapper.updateByPrimaryKeySelective(record);
            if(-1 == ret) {
                log.error("update failed: update return -1");
                return Result.returnError("update failed: update return -1");
            }
            return Result.returnSuccess();
        }catch(Exception e) {
            log.error("update error: {}" , e);
            return Result.returnError("update error:" + e);
        }
    }
}
