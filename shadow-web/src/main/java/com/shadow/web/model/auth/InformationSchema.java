package com.shadow.web.model.auth;

import lombok.Data;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/7 16:16
 * @Description:
 */
@Data
public class InformationSchema {
    /**
     * 关联表名
     */
    private String tableName;
    /**
     * 关联表中关联字段名
     */
    private String columnName;
    /**
     * 被关联表名
     */
    private String referencedTableName;
    /**
     * 外键名称
     */
    private String constraintName;
    /**
     * 被关联字段名
     */
    private String referencedColumnName;
}
