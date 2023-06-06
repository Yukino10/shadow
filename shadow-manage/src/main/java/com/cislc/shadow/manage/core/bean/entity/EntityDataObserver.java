package com.cislc.shadow.manage.core.bean.entity;

import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.enums.EntityOperation;
import com.cislc.shadow.manage.core.bean.field.DatabaseField;
import com.cislc.shadow.manage.core.bean.field.EntityField;
import com.cislc.shadow.manage.core.bean.field.ShadowField;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName EntityDataObserver
 * @Description 影子实体观察者
 * @Date 2019/10/15 14:49
 * @author szh
 **/
public class EntityDataObserver implements ShadowObserver {

    /** 观察对象所属设备的UUID **/
    private String deviceId;
    /** 观察对象的sri **/
    private String sri;

    EntityDataObserver() {

    }

    public EntityDataObserver(String deviceId, String sri) {
        this.deviceId = deviceId;
        this.sri = sri;
    }

    @Override
    public void onFieldUpdate(DatabaseField data, EntityField field) {
        // 影子中记录变更
        ShadowBean bean = ShadowFactory.getShadowBean(deviceId);
        // 变化属性
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(field.getFieldName(), field.getFieldValue());
        // 属性原值
        Map<String, Object> originalFieldMap = new HashMap<>();
        originalFieldMap.put(field.getFieldName(), field.getOriginalValue());

        ShadowField shadowField = new ShadowField(field.getClassName(), sri, fieldMap, originalFieldMap, EntityOperation.UPDATE);
        bean.addModifiedField(shadowField);
    }

}
