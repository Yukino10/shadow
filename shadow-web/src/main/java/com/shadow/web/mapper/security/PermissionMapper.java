package com.shadow.web.mapper.security;

import com.shadow.web.model.security.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper {
    List<Permission> selectByUserId(Integer userId);

    List<String> selectNamesByUserId(Integer userId);

    int selectCntByUserIdAndPermissionName(@Param("userId") Integer userId, @Param("permissionName") String permissionName);

}