package com.shadow.web.model.params;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/10 18:32
 * @Description:
 */
@Data
public class RolePermissionInsertParam {
    @NotNull(message = "角色ID不能为空")
    private Integer roleId;
    private String roleName;
    private List<Integer> functions;
}
