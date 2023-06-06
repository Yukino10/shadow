package com.shadow.web.model.params;

import com.shadow.web.model.auth.Role;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/7 15:30
 * @Description:
 */
@Data
public class RoleParams {
    @NotNull(message = "角色ID不能为空", groups = {WhenUpdateIdNotNull.class})
    private Integer roleId;
    @NotEmpty(message = "角色名称不能为空", groups = {WhenUpdateIdNotNull.class, Default.class})
    private String roleName;
    @NotEmpty(message = "角色所属版本不能为空", groups = {WhenUpdateIdNotNull.class, Default.class})
    private Integer versionType;
    @Length(max = 200, message = "描述的最大长度为200", groups = {WhenUpdateIdNotNull.class, Default.class})
    private String description;

    public Role getRole(){
        Role role = new Role();
        role.setId(this.roleId);
        role.setRoleName(this.roleName);
        role.setRoleDesc(this.description);
        role.setCoLevel(this.versionType);
        return role;
    }

}
