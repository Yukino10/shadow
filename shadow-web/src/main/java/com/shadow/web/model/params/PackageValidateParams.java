package com.shadow.web.model.params;

import lombok.Data;

/**
 * 打包校验参数
 *
 * @author szh
 * @since 2021/4/11 21:36
 **/
@Data
public class PackageValidateParams {

    /**
     * 产品id
     */
    private int productId;

    /**
     * 版本号
     */
    private String versionNo;

    /**
     * 备注
     */
    private String remark;

}
