package com.shadow.web.model.params;

import com.shadow.web.model.security.User;
import lombok.Data;

/**
 * ClassName CreateUserParams
 * Description 创建用户入参
 * Author szh
 * Date 2018-12-16 21:14
 **/
@Data
public class CreateUserParams {

    private String username;   // 登录名
    private String name;        // 姓名
    private String pwd;         //密码
    private String telephone;   // 电话
    private int companyId;      // 所属公司

    public User generateUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setName(this.username);
        user.setTelephone(this.telephone);
        return user;
    }

}
