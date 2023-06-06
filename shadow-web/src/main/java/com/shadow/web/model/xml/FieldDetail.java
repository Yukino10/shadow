package com.shadow.web.model.xml;

import lombok.Data;

/**
 * 属性信息
 *
 * @author szh
 * @since 2021/5/4 23:25
 **/
@Data
public class FieldDetail {

    /**
     * 参数类型
     */
    private String fieldType;
    /**
     * 参数名
     */
    private String fieldName;

    /**
     * 参数注释
     */
    private String fieldComment;

    public FieldDetail() {}

    public FieldDetail(String fieldType, String fieldName, String fieldComment) {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.fieldComment = fieldComment;
    }
}
