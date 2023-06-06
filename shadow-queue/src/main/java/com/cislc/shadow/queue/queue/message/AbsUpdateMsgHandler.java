package com.cislc.shadow.queue.queue.message;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.bean.field.ShadowField;
import com.cislc.shadow.manage.core.bean.shadow.ShadowDesiredDoc;
import com.cislc.shadow.manage.core.exception.ShadowException;
import com.cislc.shadow.manage.core.shadow.EntityFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 设备更新消息处理
 *
 * @author szh
 * @since 2021/5/11 12:16
 **/
@Component
public abstract class AbsUpdateMsgHandler<T extends ShadowEntity> {

    /**
     * 分发处理来自设备更新消息中的增删改逻辑
     * <p>处理handler注解中指出的entity，如果注解中指出的field字段不为空，则过滤update的所有属性</p>
     *
     * @param opsBean 通信操作内容
     * @author szh
     * @since 2021/5/11 18:26
     */
    @SuppressWarnings("unchecked")
    public final void handleMsg(ShadowOpsBean opsBean) {
        // 获取注解信息
        MsgHandler msgHandlerAnno = this.getClass().getAnnotation(MsgHandler.class);
        if (msgHandlerAnno == null) {
            throw new ShadowException("message handler has no annotation: " + this.getClass().getName());
        }
        Class<? extends ShadowEntity> targetClass = msgHandlerAnno.targetClass();   // 目标类
        String targetField = msgHandlerAnno.targetField();  // 目标属性

        ShadowDesiredDoc reportedValue = opsBean.getState().getReported();

        /* 1. 增 */
        for (ShadowField addField : reportedValue.getAdd()) {
            ShadowEntity parentEntity = EntityFactory.getEntity(addField.getParentSri());
            ShadowEntity addEntity = EntityFactory.getEntity(addField.getSri());
            if (addEntity != null && targetClass.equals(addEntity.getClass())) {
                handleAdd(parentEntity, addField.getFieldName(), (T) addEntity);
            }
        }

        /* 2. 删 */
        for (ShadowField delField : reportedValue.getDelete()) {
            ShadowEntity parentEntity = EntityFactory.getEntity(delField.getParentSri());
            ShadowEntity delEntity = EntityFactory.getEntity(delField.getSri());
            if (delEntity != null && targetClass.equals(delEntity.getClass())) {
                handleDelete(parentEntity, delField.getFieldName(), (T) delEntity);
            }
        }

        /* 3. 改 */
        for (ShadowField updateField : reportedValue.getUpdate()) {
            ShadowEntity entity = EntityFactory.getEntity(updateField.getSri());
            if (entity != null && targetClass.equals(entity.getClass())) {
                for (Map.Entry<String, Object> entry : updateField.getField().entrySet()) {
                    // 如果未指定需要过滤的属性，或指定的属性与更新属性相同
                    if (StringUtils.isBlank(targetField) || targetField.equals(entry.getKey())) {
                        handleUpdate((T) entity, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * 数据更新操作
     *
     * @param entity 操作对象
     * @param fieldName 更新属性名
     * @param updateValue 更新属性值
     * @author szh
     * @since 2021/5/11 18:39
     */
    public void handleUpdate(T entity, String fieldName, Object updateValue) {}

    /**
     * 数据删除操作
     *
     * @param parentEntity 删除对象的父对象
     * @param fieldName 父对象属性名
     * @param delEntity 删除对象
     * @author szh
     * @since 2021/5/11 18:39
     */
    public void handleDelete(ShadowEntity parentEntity, String fieldName, T delEntity) {}

    /**
     * 数据添加操作
     *
     * @param parentEntity 添加对象的父对象
     * @param fieldName 父对象属性名
     * @param addEntity 添加对象
     * @author szh
     * @since 2021/5/11 18:40
     */
    public void handleAdd(ShadowEntity parentEntity, String fieldName, T addEntity) {}

}
