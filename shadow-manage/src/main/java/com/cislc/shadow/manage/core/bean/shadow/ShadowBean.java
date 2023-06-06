package com.cislc.shadow.manage.core.bean.shadow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cislc.shadow.manage.common.utils.BeanUtils;
import com.cislc.shadow.manage.common.utils.ClassUtils;
import com.cislc.shadow.manage.common.utils.JsonUtils;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.enums.EntityOperation;
import com.cislc.shadow.manage.core.enums.ReErrorCode;
import com.cislc.shadow.manage.core.bean.field.ShadowField;
import com.cislc.shadow.manage.core.exception.NoSriException;
import com.cislc.shadow.manage.core.shadow.EntityFactory;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.sync.database.DatabaseOperation;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.cislc.shadow.manage.device.names.EntityNames.entityNames;

/**
 * ShadowBean
 * 影子管理对象
 * @since 2019/4/28 20:39
 * @author szh
 **/
@Slf4j
@Data
public class ShadowBean {

    /** 影子UUID **/
    private String deviceId;
    /** mqtt topic **/
    private String topic;
    /** coap ip **/
    private String ip;
    /** 通信协议 **/
    private Protocol protocol;
    /** 加密算法 **/
    private Encryption encryption;
    /** 影子对象 **/
    private volatile ShadowEntity data;
    /** 影子文档 **/
    private volatile ShadowDoc doc;
    /**
     * 影子变更属性
     * key: sri
     * value：变更属性
     */
    private Map<String, ShadowField> shadowField = new HashMap<>();

    public ShadowBean() {
        setDoc(new ShadowDoc(new ShadowDocState(), new ShadowDocMetadata()));
    }

    public void setData(ShadowEntity data) {
        this.data = data;
        initDocState();
    }

    /**
     * 更新数据库
     * @author szh
     * @since 2020/1/28 18:04
     */
    public void saveData() {
        DatabaseOperation operation = BeanUtils.getBean(DatabaseOperation.class);
        operation.saveEntity(data);
    }
    
    /**
     * 初始化state中reported部分
     * @author szh
     * @since 2019/7/1 14:08
     */
    private void initDocState() {
        if (null != data) {
            doc.getState().setReported(data);
        }
    }

    /**
     * 增加变更属性
     * @param field 属性信息
     * @author szh
     * @since 2019/6/19 14:28
     */
    public void addModifiedField(ShadowField field) {
        if (shadowField.containsKey(field.getSri())) {
            // 更新值
            Map<String, Object> f = shadowField.get(field.getSri()).getField();
            f.putAll(field.getField());
            // 原有值
            Map<String, Object> o = shadowField.get(field.getSri()).getOriginalField();
            o.putAll(field.getOriginalField());
        } else {
            shadowField.put(field.getSri(), field);
        }
    }

    /**
     * 清空变更属性
     * @author szh
     * @since 2019/6/19 14:28
     */
    public void clearModifiedField() {
        shadowField.clear();
    }

    /**
     * 由设备端给定值更新影子
     * @param updateValue 更新属性
     * @return 错误信息
     * @author szh
     * @since 2019/5/2 15:55
     */
    public synchronized ReErrorCode updateShadowByDevice(ShadowDesiredDoc updateValue) {
        try {
            /* step1: 更新影子属性 */
            // metadata
            long current = System.currentTimeMillis();
            ShadowDocMetadata metadata = doc.getMetadata();
            if (null == metadata) {
                metadata = new ShadowDocMetadata();
                doc.setMetadata(metadata);
            }

            // 增加
            for (ShadowField addField : updateValue.getAdd()) {
                // 父类实体存在且要添加的实体不存在
                if (!StringUtils.isEmpty(addField.getParentSri()) &&
                        EntityFactory.isSriExist(addField.getParentSri()) &&
                        !EntityFactory.isSriExist(addField.getSri())) {
                    // 获取父类实体
                    ShadowEntity parentEntity = EntityFactory.getEntity(addField.getParentSri());
                    // 实体补充sri和topic
                    Map<String, Object> field = addField.getField();
                    field.put("sri", addField.getSri());
                    field.put(ShadowConst.DEVICE_ID, deviceId);
                    // 实体属性反序列化，强转成为用户定义实体
                    int disableDecimalFeature = JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask();
                    String json = JsonUtils.toJSONString(addField.getField());
                    Class<?> entityClass = Class.forName(ClassUtils.getEntityPackageName(addField.getClassName()));
                    ShadowEntity entity = JSONObject.parseObject(json, entityClass, disableDecimalFeature);
                    // 影子更新
                    if (null != entity) {
                        // data
                        EntityFactory.injectEntities(entity, deviceId, entityNames);
                        ClassUtils.setAdd(parentEntity, addField.getFieldName(), entity);
                        // metadata
                        Map<String, Object> metadataTime = new HashMap<>();
                        metadataTime.put(ShadowConst.DOC_KEY_TIMESTAMP, current);
                        metadata.getReported().put(addField.getParentSri(), metadataTime);
                    }
                }
            }
            // 删除
            for (ShadowField delField : updateValue.getDelete()) {
                if (null != delField.getSri() && EntityFactory.isSriExist(delField.getSri()) &&
                        null != delField.getParentSri() && EntityFactory.isSriExist(delField.getParentSri())) {
                    // data
                    ShadowEntity parentEntity = EntityFactory.getEntity(delField.getParentSri());
                    ShadowEntity delEntity = EntityFactory.getEntity(delField.getSri());
                    ClassUtils.setRemove(parentEntity, delField.getFieldName(), delEntity);
                    // metadata
                    Map<String, Object> metadataTime = new HashMap<>();
                    metadataTime.put(ShadowConst.DOC_KEY_TIMESTAMP, current);
                    metadata.getReported().put(delField.getParentSri(), metadataTime);
                }
            }
            // 更新
            for (ShadowField updateField : updateValue.getUpdate()) {
                if (null != updateField.getSri() && EntityFactory.isSriExist(updateField.getSri())) {
                    ShadowEntity entity = EntityFactory.getEntity(updateField.getSri());
                    if (null != entity) {
                        for (Map.Entry<String, Object> entry : updateField.getField().entrySet()) {
                            String fieldName = entry.getKey();
                            boolean updateSuccess = ClassUtils.setValue(entity, fieldName, entry.getValue());
                            // 更新ip
                            if (ShadowConst.IP.equals(fieldName)) {
                                String deviceIp = (String) updateField.getField().get(fieldName);
                                if (!StringUtils.isEmpty(deviceIp)) {
                                    this.ip = deviceIp;
                                }
                            }
                            if (!updateSuccess) {
                                // 影子属性回退
                                shadowCommitRevert();
                                return ReErrorCode.SHADOW_ATTR_WRONG;
                            } else {
                                // metadata
                                Map<String, Object> metadataTime = new HashMap<>();
                                metadataTime.put(ShadowConst.DOC_KEY_TIMESTAMP, current);
                                metadata.getReported().put(updateField.getSri(), metadataTime);
                            }
                        }
                    }
                }
            }

            /* step2 更新数据库 */
            DatabaseOperation operation = BeanUtils.getBean(DatabaseOperation.class);
            operation.saveEntity(data);

            /* step3 更新文档属性 */
            // 序列化保存状态
            doc.getState().setReported(data);
            // 更新时间戳
            doc.setTimestamp(current);

            return null;
        } catch (Exception e) {
            log.error("影子更新失败：" + e.getMessage());
            return ReErrorCode.SERVER_ERROR;
        }
    }

    /**
     * 设备端更新影子成功
     * @return 是否成功
     * @author szh
     * @since 2019/5/3 11:27
     */
    public synchronized ReErrorCode clearDesired() {
        // 清空desire
        doc.clearDesired();
        // 清空变更
        shadowField.clear();
        if (null != doc.getMetadata()) {
            doc.getMetadata().getDesired().clear();
            doc.getMetadata().getDesired().put(ShadowConst.DOC_KEY_TIMESTAMP, System.currentTimeMillis());
        }
        return null;
    }

    /**
     * 由服务器端影子更新文档
     * @param timestamp 时间戳
     * @return 是否出错
     * @author szh
     * @since 2019/5/2 17:28
     */
    public synchronized ReErrorCode updateShadowByServer(long timestamp) {
        // 对照list的变更
        compareAttr(data, (ShadowEntity) doc.getState().getReported(data.getClass()));

        // 更新文档
        // desired
        for (ShadowField sf : shadowField.values()) {
            switch (sf.getOperation()) {
                case ADD:
                    doc.getState().getDesired().getAdd().add(sf);
                    break;

                case DELETE:
                    doc.getState().getDesired().getDelete().add(sf);
                    break;

                case UPDATE:
                    doc.getState().getDesired().getUpdate().add(sf);
                    break;
            }
        }
        // metadata
        for (String sri : shadowField.keySet()) {
            Map<String, Object> metadataTime = new HashMap<>();
            metadataTime.put(ShadowConst.DOC_KEY_TIMESTAMP, timestamp);
            doc.getMetadata().getDesired().put(sri, metadataTime);
        }

        // 回退影子对象，当设备端修改成功之后才更改影子对象
        shadowCommitRevert();

        // 更新时间戳
        doc.setTimestamp(timestamp);
        // 更新版本
        doc.addUpVersion();

        return null;
    }

    /**
     * 更新影子修改时间
     *
     * @author szh
     * @since 2020/12/25 11:45
     */
    public synchronized void updateTimestamp() {
        doc.setTimestamp(System.currentTimeMillis());
    }

    /**
     * 获取影子最后更新时间
     *
     * @return 更新时间
     * @author szh
     * @since 2020/12/25 11:46
     */
    public long getLastUpdateTime() {
        return doc.getTimestamp();
    }

    /**
     * 清除绑定码
     *
     * @author szh
     * @since 2020/12/26 14:05
     */
    public void clearBindCode() {
        data.setBindCode(null);
    }

    /**
     * 设备是否已绑定
     *
     * @return 是否绑定
     * @author szh
     * @since 2020/12/26 14:10
     */
    public boolean isBound() {
        return StringUtils.isBlank(data.getBindCode());
    }

    /**
     * 影子提交修改部分版本回退
     * @author szh
     * @since 2019/4/30 17:15
     */
    private void shadowCommitRevert() {
        for (ShadowField sf : shadowField.values()) {
            switch (sf.getOperation()) {
                case ADD:
                    // 新增的删掉
                    Object addParentObj = BeanUtils.getBean(sf.getParentSri());
                    Object addObj = BeanUtils.getBean(sf.getSri());
                    if (null != addParentObj && null != addObj) {
                        ClassUtils.setRemove(addParentObj, sf.getFieldName(), addObj);
                        EntityFactory.destroyEntity(sf.getSri());
                    }
                    break;

                case DELETE:
                    // 删除的加回来
                    Object delParentObj = BeanUtils.getBean(sf.getParentSri());
                    Object delObj = BeanUtils.getBean(sf.getSri());
                    if (null != delParentObj && null != delObj) {
                        ClassUtils.setAdd(delParentObj, sf.getFieldName(), delObj);
                    }
                    break;

                case UPDATE:
                    // 更新的恢复
                    Object updateObj = BeanUtils.getBean(sf.getSri());
                    if (null != updateObj) {
                        for (String updateField : sf.getOriginalField().keySet()) {
                            ClassUtils.setValue(updateObj, updateField, sf.getOriginalField().get(updateField));
                        }
                    }
                    break;
            }
        }
        // 清空变更
        shadowField.clear();
    }

    /**
     * 影子版本全部回退
     * @author szh
     * @since 2019/7/22 16:00
     */
    public void shadowRevert() {
        // 对照list的变更
        compareAttr(data, (ShadowEntity) doc.getState().getReported(data.getClass()));
        // 回退影子对象
        shadowCommitRevert();
        // 清空期望数据
        clearDesired();
    }

    /**
     * 比较实体的list
     * @param entity 实体
     * @param doc 影子文档
     * @author szh
     * @since 2019/6/26 19:01
     */
    private void compareAttr(ShadowEntity entity, ShadowEntity doc) {
        if (null == entity) {
            return;
        }
        // 检查实体sri
        if (StringUtils.isEmpty(entity.getSri())) {
            throw new NoSriException();
        }

        // 遍历对比
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            String fieldType = f.getType().getSimpleName();
            String fieldName = f.getName();
            if ("Set".equals(fieldType)) {
                // 是list属性则比照内存中的对象与影子文档中的不同
                Set<ShadowEntity> newSet = ClassUtils.getValue(entity, fieldName);
                Set<ShadowEntity> oldSet = ClassUtils.getValue(doc, fieldName);
                if (null != doc) {
                    compareSet(newSet, oldSet, entity.getSri(), fieldName);
                }
            } else if (entityNames.contains(fieldType)) {
                // 是受管理的实体则递归遍历继续比照内部的list
                ShadowEntity childEntity = ClassUtils.getValue(entity, fieldName);
                ShadowEntity childDoc = ClassUtils.getValue(doc, fieldName);
                compareAttr(childEntity, childDoc);
                // 剔除嵌套实体中重复添加的更改记录
                if (null == childEntity && null != childDoc) {
                    // 原来是null，赋值新建对象
                    shadowField.remove(childDoc.getSri());
                } else if (null != childEntity && null == childDoc) {
                    // 原来有值，赋值null
                    shadowField.remove(childEntity.getSri());
                } else if (null != childEntity && !childEntity.equals(childDoc)) {
                    // 原来有值，赋值新对象
                    shadowField.remove(childEntity.getSri());
                    shadowField.remove(childDoc.getSri());
                }
            }
        }
    }

    /**
     * 对比set变化
     * @param newSet 新set
     * @param oldSet 原set
     * @param parentSri 父级sri
     * @author szh
     * @since 2019/6/26 17:07
     */
    private void compareSet(Set<ShadowEntity> newSet, Set<ShadowEntity> oldSet, String parentSri, String listName) {
        if ((null == oldSet || oldSet.isEmpty()) && (null == newSet || newSet.isEmpty())) {
            return;
        }

        // 要删除的
        Set<ShadowEntity> toDelete = null;
        // 要增加的
        Set<ShadowEntity> toAdd = null;

        if ((null == oldSet || oldSet.isEmpty())) {
            // 原list为空，新list有内容，则全部为新增
            toAdd = newSet;
        } else if ((null == newSet || newSet.isEmpty())) {
            // 新list为空，原list有内容，则全部删除
            toDelete = oldSet;
        } else {
            toDelete = oldSet.stream().filter(item -> !entitySetContains(newSet, item)).collect(Collectors.toSet());
            toAdd = newSet.stream().filter(item -> !entitySetContains(oldSet, item)).collect(Collectors.toSet());
        }

        // 删除
        if (null != toDelete) {
            for (ShadowEntity entity : toDelete) {
                ShadowField delField = new ShadowField(entity.getClass().getSimpleName(), entity.getSri(), listName,
                        parentSri, EntityOperation.DELETE);
                shadowField.put(entity.getSri(), delField);
            }
        }
        // 增加
        if (null != toAdd) {
            for (ShadowEntity entity : toAdd) {
                ShadowField addField = new ShadowField(entity.getClass().getSimpleName(),
                        entity.getSri(), listName, parentSri,
                        ClassUtils.getValueMap(entity, Collections.singletonList("databaseFieldMap")), EntityOperation.ADD);
                shadowField.put(entity.getSri(), addField);
            }
        }

        // 继续比较list中没有删除和修改的部分的实体变化
        if (null != newSet && null != oldSet) {
            // 获得相同部分
            for (ShadowEntity entity : oldSet) {
                ShadowEntity sameEntity = entitySetExist(newSet, entity);
                if (sameEntity != null) {
                    compareAttr(sameEntity, entity);
                }
            }
        }
    }

    /**
     * 获取实体在list中的位置
     * @param list 实体列表
     * @param entity 实体
     * @return 位置下标
     * @author szh
     * @since 2019/10/17 15:34
     */
    private int entityListIndex(List<ShadowEntity> list, ShadowEntity entity) {
        if (null == entity) {
            return -1;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (entity.getSri().equals(list.get(i).getSri())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 获取实体在set中是否存在相同的
     *
     * @param set 实体set
     * @param entity 查询的实体
     * @return 存在则返回实体，否则返回null
     */
    private ShadowEntity entitySetExist(Set<ShadowEntity> set, ShadowEntity entity) {
        if (null != entity) {
            for (ShadowEntity entityInSet : set) {
                if (entity.getSri().equals(entityInSet.getSri())) {
                    return entityInSet;
                }
            }
        }
        return null;
    }

    /**
     * 实体在列表中是否存在
     * @param set 实体列表
     * @param entity 实体
     * @return 是否存在
     * @author szh
     * @since 2019/10/17 15:34
     */
    private boolean entitySetContains(Set<ShadowEntity> set, ShadowEntity entity) {
        return entitySetExist(set, entity) != null;
    }

    /**
     * 设置ip
     * @param ip ip
     * @author szh
     * @since 2020/1/28 17:49
     */
    public void setIp(String ip) {
        this.ip = ip;
        data.setIp(ip);
    }

}
