package com.shadow.web.model.result;

import lombok.Data;

import java.util.Date;

/**
 * @Auther: wangzhendong
 * @Date: 2018/11/22 11:26
 * @Description:
 */
@Data
public class UserInfo {
    //cm_user的id
    private Integer id;
    //cm_user的登录名
    private String username;
    //cm_user的用户姓名
    private String name;
    //cm_user的手机号
    private String telephone;
    // 用户角色名称
    private Integer roleId;
    private String roleName;
    private Integer versionType;
    //cm_user的create_time
    private Date registerTime;
    //cm_user的last_login_out_time
    private Date lastLoginOutTime;
    //未读消息条数s
    private Integer notifyCount;
}
