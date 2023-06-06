package com.shadow.web.model.xml;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EntityDetail
 * 影子实体属性
 *
 * @since 2019/6/17 10:30
 * @author szh
 **/
@Data
public class EntityDetail {

    /**
     * 类名
     */
    private String className;

    /**
     * 类注释
     */
    private String entityComment;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 类属性
     */
    private List<FieldDetail> fieldDetailList = new ArrayList<>();
    /**
     * 类属性与数据库字段映射关系
     */
    private Map<String, DatabaseField> databaseFieldMap = new HashMap<>();

    public EntityDetail() {}

    public EntityDetail(String className, String entityComment, String tableName, List<FieldDetail> fieldDetailList, Map<String, DatabaseField> databaseFieldMap) {
        this.className = className;
        this.entityComment = entityComment;
        this.tableName = tableName;
        this.fieldDetailList = fieldDetailList;
        this.databaseFieldMap = databaseFieldMap;
    }
}
