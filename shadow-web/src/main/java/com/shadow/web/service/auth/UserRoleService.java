package com.shadow.web.service.auth;

import com.shadow.web.mapper.auth.UserRoleMapper;
import com.shadow.web.model.auth.UserRole;
import com.shadow.web.model.auth.UserRoleExample;
import com.shadow.web.model.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/9 15:39
 * @Description:
 */
@Service
public class UserRoleService {
    private static Logger log = LoggerFactory.getLogger(UserRoleService.class);

    @Resource
    UserRoleMapper mapper;

    public Result<List<UserRole>> findByExample(UserRoleExample example) {
        try {
            return Result.returnSuccess(mapper.selectByExample(example));
        }catch(Exception e) {
            log.error("findByExample error: {}" , e);
            return Result.returnError("findByExample error:" + e);
        }
    }

    public Result<UserRole> findById(Integer id) {
        try {
            return Result.returnSuccess(mapper.selectByPrimaryKey(id));
        }catch(Exception e) {
            log.error("findById error: {}" , e);
            return Result.returnError("findById error:" + e);
        }
    }

    public Result<Integer> countByExample(UserRoleExample example){
        try {
            return Result.returnSuccess(mapper.countByExample(example));
        }catch(Exception e) {
            log.error("countByExample error: {}" , e);
            return Result.returnError("countByExample error:" + e);
        }
    }

    public Result<Integer> create(UserRole record) {
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

    public Result<Boolean> update(UserRole record) {
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
}
