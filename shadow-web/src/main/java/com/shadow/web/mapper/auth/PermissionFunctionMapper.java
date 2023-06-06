package com.shadow.web.mapper.auth;

import com.shadow.web.model.auth.PermissionFunction;
import com.shadow.web.model.auth.PermissionFunctionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PermissionFunctionMapper {
    int countByExample(PermissionFunctionExample example);

    int deleteByExample(PermissionFunctionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PermissionFunction record);

    int insertSelective(PermissionFunction record);

    List<PermissionFunction> selectByExample(PermissionFunctionExample example);

    PermissionFunction selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PermissionFunction record, @Param("example") PermissionFunctionExample example);

    int updateByExample(@Param("record") PermissionFunction record, @Param("example") PermissionFunctionExample example);

    int updateByPrimaryKeySelective(PermissionFunction record);

    int updateByPrimaryKey(PermissionFunction record);
}