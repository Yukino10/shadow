package com.cislc.shadow.manage.core.shadow;

import com.cislc.shadow.manage.common.utils.BeanUtils;
import com.cislc.shadow.manage.common.utils.ClassUtils;
import com.cislc.shadow.manage.core.exception.NoSriException;
import com.cislc.shadow.manage.core.exception.NoDeviceIdException;
import com.cislc.shadow.manage.core.bean.entity.EntityDataObserver;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EntityFactory
 * 实体相关操作
 * @since 2019/7/4 9:38
 * @author szh
 **/
public class EntityFactory {

    /**
     * 保存实体的SRI
     */
    private static final Set<String> entitySriSet = new HashSet<>();

    /**
     * 判断sri是否存在
     * @param sri 实体sri
     * @return 存在
     * @author szh
     * @since 2019/7/2 15:05
     */
    public static boolean isSriExist(String sri) {
        return entitySriSet.contains(sri);
    }

    /**
     * 注入影子的各个部分
     * @param shadowEntity 影子部分实体
     * @throws NoDeviceIdException 实体无UUID异常
     * @throws NoSriException 实体无sri异常
     * @return 是否成功
     * @author szh
     * @since 2019/6/16 19:51
     */
    public static boolean injectEntity(ShadowEntity shadowEntity) {
        if (StringUtils.isEmpty(shadowEntity.getDeviceId())) {
            throw new NoDeviceIdException();
        }
        // 实体sri不合法就生成一个
        if (!shadowEntity.checkSRI()) {
            shadowEntity.generateSRI();
        }
        String sri = shadowEntity.getSri();
        if (entitySriSet.contains(sri)) {
            return false;
        }
        entitySriSet.add(sri);
        BeanUtils.injectExistBean(shadowEntity, sri);
        // 增加订阅
        shadowEntity.addObserver(new EntityDataObserver(shadowEntity.getDeviceId(), sri));
        return true;
    }

    /**
     * 递归注入所有实体
     * @param shadowEntity 实体
     * @param deviceId 设备id
     * @param entityNames 所有实体类名
     * @author szh
     * @since 2019/6/18 11:23
     */
    public static void injectEntities(ShadowEntity shadowEntity, String deviceId, List<String> entityNames) {
        shadowEntity.setDeviceId(deviceId);
        // 注入自身
        injectEntity(shadowEntity);
        // 获取所有属性类型
        Class<?> entityClass = shadowEntity.getClass();
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldType = field.getType().getSimpleName();
            if (entityNames.contains(fieldType)) {
                // 如果是实体直接注入
                ShadowEntity shadowField = ClassUtils.getValue(shadowEntity, field.getName());
                if (null != shadowField) {
                    injectEntities(shadowField, deviceId, entityNames);
                }
            } else if ("Set".equals(fieldType)) {
                // 如果是list，遍历注入
                Set<ShadowEntity> shadowFields = ClassUtils.getValue(shadowEntity, field.getName());
                if (null != shadowFields) {
                    for (ShadowEntity shadowField : shadowFields) {
                        injectEntities(shadowField, deviceId, entityNames);
                    }
                }
            }
        }
    }

    /**
     * 删除实体
     * @param beanName 实体名
     * @author szh
     * @since 2020/1/30 21:38
     */
    public static void destroyEntity(String beanName) {
        BeanUtils.destroyBean(beanName);
        entitySriSet.remove(beanName);
    }

    /**
     * 清空实体
     * @author szh
     * @since 2019/6/18 0:09
     */
    public static void destroyEntities() {
        for (String beanName : entitySriSet) {
            BeanUtils.destroyBean(beanName);
        }
        entitySriSet.clear();
    }

    /**
     * 通过sri获取实体
     *
     * @param sri sri
     * @return 影子实体
     * @author szh
     * @since 2019/7/2 15:11
     */
    @SuppressWarnings("unchecked")
    public static <T extends ShadowEntity> T getEntity(String sri) {
        if (!isSriExist(sri)) {
            return null;
        }
        return (T) BeanUtils.getBean(sri);
    }

}
