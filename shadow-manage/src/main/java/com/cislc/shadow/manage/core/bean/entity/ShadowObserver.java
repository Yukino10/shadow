package com.cislc.shadow.manage.core.bean.entity;

import com.cislc.shadow.manage.core.bean.field.DatabaseField;
import com.cislc.shadow.manage.core.bean.field.EntityField;

/**
 * ShadowObserver
 * 影子观察者
 *
 * @author szh
 * @since 2019/10/15 14:44
 **/
public interface ShadowObserver {

    /**
     * 实体属性更新
     *
     * @param data 数据库映射
     * @param field 实体属性
     * @author szh
     * @since 2019/6/19 16:56
     */
    void onFieldUpdate(DatabaseField data, EntityField field);

}
