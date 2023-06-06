package com.cislc.shadow.manage.core.shadow;

import com.cislc.shadow.manage.common.utils.BeanUtils;
import com.cislc.shadow.manage.common.utils.UUIDUtils;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.exception.BindCodeExistException;
import com.cislc.shadow.manage.core.exception.NoTopicException;
import com.cislc.shadow.utils.enums.Protocol;
import com.cislc.shadow.utils.mqtt.TopicUtils;
import com.cislc.shadow.utils.mqtts.MqttFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Semaphore;

import static com.cislc.shadow.manage.device.names.EntityNames.entityNames;

/**
 * @author szh
 * @ClassName ShadowFactory
 * @Description 影子管理
 * @Date 2019/2/1 18:09
 **/
@Slf4j
public class ShadowFactory {

    /** 保存影子device id */
    private static final Set<String> beanSet = new HashSet<>();
    /** 保存影子对象类名与device id关系 key: class name, value: device id list **/
    private static final Map<String, ArrayList<String>> classMap = new HashMap<>();
    /** 保存影子的信号量*/
    private static final Map<String, Semaphore> semaphoreMap = new HashMap<>();
    /** 保存线程名称与对应的deviceId*/
    private static final Map<String, ArrayList<String>> threadMap = new HashMap<>();
    /** 保存绑定标识符与device id关系 key: bind code, value: device id */
    private static final Map<String, String> bindCodeMap = new HashMap<>();

    /**
     * 注入影子到容器
     *
     * @param bean 要管理的影子
     * @return 设备id
     * @author szh
     * @Date 2019/2/1 18:09
     */
    private static String injectShadow(ShadowBean bean) {
        /* 1. 检查是否为bean实体 */
        if (!bean.getData().isDevice()) {
            throw new RuntimeException("bean is not a device entity");
        }
        /* 2. 检查bean是否存在 */
        String deviceId = bean.getDeviceId();
        if (beanSet.contains(deviceId)) {
            throw new RuntimeException("bean with device id: " + deviceId + " already exists");
        }
        /* 3. 检查bindCode */
        String bindCode = bean.getData().getBindCode();
        if (!StringUtils.isEmpty(bindCode)) {
            if (bindCodeMap.containsKey(bindCode)) {
                throw new BindCodeExistException(bindCode);
            }
            bindCodeMap.put(bindCode, deviceId);
        }

        /* 3. bean注入 */
        // 设置通信协议
        bean.setProtocol(bean.getData().getProtocol());
        // 设置加密算法
        bean.setEncryption(bean.getData().getEncryption());
        // 保存bean的设备id
        beanSet.add(bean.getDeviceId());
        // 保存类名关系
        String className = bean.getData().getClass().getSimpleName();
        if (classMap.containsKey(className)) {
            classMap.get(className).add(bean.getDeviceId());
        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add(bean.getDeviceId());
            classMap.put(className, list);
        }
        // 标注信号量
        semaphoreMap.put(deviceId,new Semaphore(1));
        BeanUtils.injectExistBean(bean, bean.getDeviceId());

        /* 4. entity注入 */
        EntityFactory.injectEntities(bean.getData(), deviceId, entityNames);

        /* 5. mqtt订阅 */
        if (Protocol.MQTT.equals(bean.getProtocol())) {
            String topic = bean.getTopic();
            if (!StringUtils.isEmpty(topic)) {
                // 订阅MQTT topic
                MqttFactory.subscribe(TopicUtils.getUpdateTopic(topic), 0);
            } else {
                throw new NoTopicException();
            }
        }

        /* 6. 保存ip */
        String ip = bean.getData().getIp();
        if (!StringUtils.isEmpty(ip)) {
            bean.setIp(ip);
        }
        return deviceId;
    }

    /**
     * 注入影子到容器
     *
     * @param className 设备类名
     * @param topic mqtt订阅主题
     * @return 设备id
     * @author szh
     * @date 2019/2/8 21:53
     */
    public static String injectShadow(String className, String bindCode, String topic) {
        try {
            /* 1. 实例化device对象 **/
            Class<?> shadowClass = Class.forName(className);
            ShadowEntity shadow = (ShadowEntity) shadowClass.newInstance();
            shadow.setDeviceId(UUIDUtils.getUUID());
            if (!StringUtils.isEmpty(topic)) {
                shadow.setTopic(topic);
            }

            /* 2. bean注入 **/
            return injectShadow(shadow, bindCode, topic);

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("injectShadow failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * @Description 注入影子到容器
     * @param data  管理对象
     * @param topic mqtt topic
     * @return 设备id
     * @author szh
     * @Date 2019/5/5 14:14
     */
    static String injectShadow(ShadowEntity data, String bindCode, String topic) {
        // 检查bean是否存在
        if (!StringUtils.isEmpty(data.getDeviceId())
                && beanSet.contains(data.getDeviceId())) {
            throw new RuntimeException("bean already exists");
        }

        // 生成deviceId
        if (StringUtils.isEmpty(data.getDeviceId())) {
            data.setDeviceId(UUIDUtils.getUUID());
        }

        ShadowBean shadowBean = new ShadowBean();
        shadowBean.setData(data);
        shadowBean.setDeviceId(data.getDeviceId());

        // 如果没有topic则使用deviceId
        if (StringUtils.isEmpty(topic)) {
            topic = data.getDeviceId();
        }
        data.setTopic(topic);
        shadowBean.setTopic(topic);

        // 设置bindCode
        data.setBindCode(bindCode);

        return injectShadow(shadowBean);
    }

    /**
     * @Description 批量注入影子
     * @param dataMap 影子与主题
     * @return 是否成功
     * @author szh
     * @Date 2019/6/13 14:45
     */
    public static boolean batchInjectShadow(Map<String, ShadowEntity> dataMap) {
        for (Map.Entry<String, ShadowEntity> entry : dataMap.entrySet()) {
            ShadowEntity entity = entry.getValue();
            String id = injectShadow(entity, entity.getBindCode(), entity.getTopic());
            if (StringUtils.isEmpty(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通过主题获取相应的影子
     *
     * @param deviceId 设备id
     * @return 影子
     * @author szh
     * @date 2019/2/1 18:44
     */
    public static ShadowBean getShadowBean(String deviceId) {
        return (ShadowBean) BeanUtils.getBean(deviceId);
    }

    /**
     * @Description 通过影子数据类型获取影子列表
     * @param dataClass 影子数据两类型
     * @return 影子列表
     * @author szh
     * @Date 2019/8/8 13:11
     */
    static List<ShadowBean> getShadowBeans(Class<?> dataClass) {
        List<ShadowBean> beanList = new ArrayList<>();
        String className = dataClass.getSimpleName();

        if (classMap.containsKey(className)) {
            for (String id : classMap.get(className)) {
                beanList.add((ShadowBean) BeanUtils.getBean(id));
            }
        }
        return beanList;
    }

    /**
     * 获取所有影子列表
     *
     * @return 影子列表
     * @author szh
     * @since 2020/12/25 14:48
     */
    public static List<ShadowBean> getShadowBeans() {
        List<ShadowBean> beanList = new ArrayList<>();
        for (String deviceId : beanSet) {
            beanList.add((ShadowBean) BeanUtils.getBean(deviceId));
        }
        return beanList;
    }

    /**
     * @Description 通过绑定标识符获取设备id
     * @param bindCode 绑定标识符
     * @return deviceId
     * @author szh
     * @Date 2020/1/28 17:19
     */
    public static String getDeviceIdByBindCode(String bindCode) {
        return bindCodeMap.get(bindCode);
    }

    /**
     * @Description 删除bindCode
     * @param bindCode 绑定标识符
     * @author szh
     * @Date 2020/1/28 17:57
     */
    public static void removeBindCode(String bindCode) {
        bindCodeMap.remove(bindCode);
    }

    /**
     * 获取信号量
     * @param deviceId 设备id
     * @return 信号量
     */
    static Semaphore getSemaphore(String deviceId){
        return semaphoreMap.get(deviceId);
    }

    public static ArrayList<String> getThreadTopic(String threadName){
        return threadMap.get(threadName);
    }

    public static Map<String,ArrayList<String>> getThreadMap(){
        return threadMap;
    }



}
