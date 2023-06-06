package com.shadow.web.mapper.security;

import com.shadow.web.model.result.UserInfo;
import com.shadow.web.model.security.User;
import com.shadow.web.model.security.UserExample;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    //-------------------------------------------------------
    /**
     * @return 按等级查询用户列表
     * @auther 10413
     * @date 2021-04-10 15:59
     */
    List<UserInfo> searchUsersByClass(Map<String, Object> input);

    /**
     * @return 校验登录名是否存在
     * @auther 10413
     * @date 2021-04-10 22:43
     */
    Integer validLoginName(@Param("loginName") String loginName);

    UserInfo queryUserInfoByPrimaryKey(Integer id);
}