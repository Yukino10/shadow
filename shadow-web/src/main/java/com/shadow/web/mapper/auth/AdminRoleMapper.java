package com.shadow.web.mapper.auth;

import com.shadow.web.model.auth.Role;
import com.shadow.web.model.params.RoleParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * InterfaceName AdminRoleMapper
 * Author szh
 * Date 2018-12-14 21:10
 **/
public interface AdminRoleMapper {

    /**
     * @Description 根据登录员工权限筛选可看到的角色列表
     * @Author szh
     * @Date 2018/12/14 21:34
     **/
    List<RoleParams> selectRoleByClass(@Param("loginUserId") int loginUserId);

    /**
     * 查找用户角色
     *
     * @param userId 用户id
     * @return 角色
     * @author szh
     * @since 2021/4/27 12:28
     */
    List<Role> selectByUserId(@Param("userId") int userId);

}
