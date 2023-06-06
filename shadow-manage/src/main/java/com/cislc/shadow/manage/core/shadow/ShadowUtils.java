package com.cislc.shadow.manage.core.shadow;

import com.cislc.shadow.manage.common.utils.BeanUtils;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.enums.ReErrorCode;
import com.cislc.shadow.manage.core.exception.NoDeviceIdException;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.sync.database.DatabaseOperation;
import com.cislc.shadow.manage.core.sync.push.ControlPush;
import com.cislc.shadow.utils.bean.comm.CommParams;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.cislc.shadow.manage.device.names.EntityNames.entityNames;

/**
 * ShadowUtils
 * 影子操作工具类
 *
 * @since 2019/7/4 9:46
 * @author szh
 **/
public class ShadowUtils {

    private static final ControlPush controlPush = new ControlPush();

    /**
     * 增加影子对象
     *
     * @param data 影子对象
     * @param bindCode 绑定标识符
     * @return 设备id
     *
     * @author szh
     * @since 2019/7/5 9:22
     */
    public static String addShadow(ShadowEntity data, String bindCode) {
        return addShadow(data, bindCode, null);
    }

    /**
     * 增加影子对象
     *
     * @param data 影子对象
     * @param bindCode 绑定标识符
     * @param topic 主题
     * @return 设备id
     *
     * @author szh
     * @since 2019/7/5 9:22
     */
    public static String addShadow(ShadowEntity data, String bindCode, String topic) {
        // 注入影子
        return ShadowFactory.injectShadow(data, bindCode, topic);
    }

    /**
     * 获取影子时加锁
     *
     * @param shadowBean 影子
     * @return 影子对象
     *
     * @author szh
     * @since 2019/8/9 23:42
     */
    @SuppressWarnings("unchecked")
    private static <T extends ShadowEntity> T dealGetShadow(ShadowBean shadowBean) {
        if (null != shadowBean) {
            String deviceId = shadowBean.getDeviceId();

            /* 获取影子对象的信号量 */
            Semaphore semaphore = ShadowFactory.getSemaphore(deviceId);
            /* 设置线程使用的deviceId */
            String threadName = Thread.currentThread().getName();
            ArrayList<String> threadDeviceIds = ShadowFactory.getThreadTopic(threadName);
            if (threadDeviceIds == null) {
                threadDeviceIds = new ArrayList<>();
                Map<String, ArrayList<String>> threadMap = ShadowFactory.getThreadMap();
                threadMap.put(threadName, threadDeviceIds);
            }
            threadDeviceIds.add(deviceId);
            try {
                if (null != semaphore) {
                    while (!semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                        ShadowUtils.class.wait();
                    }
                    return (T) shadowBean.getData();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                semaphore.release();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取影子对象
     *
     * @param deviceId 设备id
     * @return 对象
     *
     * @author szh
     * @since 2019/5/2 20:46
     */
    public static synchronized <T extends ShadowEntity> T getShadow(String deviceId) {
        ShadowBean shadowBean = ShadowFactory.getShadowBean(deviceId);
        return dealGetShadow(shadowBean);

    }

    /**
     * 按类型获取影子对象列表
     *
     * @param dataClass 影子类型
     * @return 对象列表
     *
     * @author szh
     * @since 2019/8/8 19:13
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getShadowList(Class<T> dataClass) {
        List<ShadowBean> shadowBeans = ShadowFactory.getShadowBeans(dataClass);
        List<T> shadowEntities = new ArrayList<>();

        for (ShadowBean bean : shadowBeans) {
            T entity = (T) dealGetShadow(bean);
            if (null != entity) {
                shadowEntities.add(entity);
            }
        }
        return shadowEntities;
    }

    /**
     * 持久化实体修改
     *
     * @param deviceId 设备id
     *
     * @author szh
     * @since 2019/7/22 16:30
     */
    public static void save(String deviceId) {
        ShadowBean shadowBean = ShadowFactory.getShadowBean(deviceId);
        if (null != shadowBean) {
            DatabaseOperation operation = BeanUtils.getBean(DatabaseOperation.class);
            operation.saveEntity(shadowBean.getData());
        }
    }

    /**
     * 通过主题更新影子文档
     *
     * @param deviceId 设备id
     * @return 是否成功
     *
     * @author szh
     * @since 2019/5/9 10:46
     */
    public static synchronized ReErrorCode commit(String deviceId) {
        ShadowBean shadowBean = ShadowFactory.getShadowBean(deviceId);
        if (null != shadowBean) {
            long current = System.currentTimeMillis();
            return shadowBean.updateShadowByServer(current);
//        if (null == errorCode) {
//            // 保存到数据库
//            DatabaseQueue.amqpSave(shadowBean.getData());
//        }
        } else {
            return null;
        }
    }

    /**
     * 通过主题更新影子文档并下发
     *
     * @param deviceId 设备id
     * @return 是否成功
     *
     * @author szh
     * @since 2019/5/2 16:17
     */
    public static synchronized ReErrorCode commitAndPush(String deviceId) {
        ShadowBean shadowBean = ShadowFactory.getShadowBean(deviceId);
        if (null != shadowBean) {
            // 检查是否绑定
            if (!StringUtils.isEmpty(shadowBean.getData().getBindCode())) {
                return ReErrorCode.DEVICE_NOT_BIND;
            }
            long current = System.currentTimeMillis();
            ReErrorCode error = shadowBean.updateShadowByServer(current);
            if (null == error) {
                // 保存到数据库
//            DatabaseQueue.amqpSave(shadowBean.getData());
                // 更新版本
                shadowBean.getDoc().addUpVersion();
                // 下发状态
                CommParams commParams = new CommParams(shadowBean.getProtocol(), shadowBean.getIp(),
                        shadowBean.getTopic(), shadowBean.getEncryption(), shadowBean.getDeviceId());
                controlPush.push(deviceId, commParams, shadowBean.getDoc(), current);
            }
            return error;
        } else {
            return null;
        }
    }

    /**
     * 下发影子修改
     *
     * @param deviceId 设备id
     * @return 是否成功
     *
     * @author szh
     * @since 2019/5/9 10:44
     */
    public static ReErrorCode push(String deviceId) {
        ShadowBean shadowBean = ShadowFactory.getShadowBean(deviceId);
        if (null != shadowBean) {
            // 检查是否绑定
            if (!StringUtils.isEmpty(shadowBean.getData().getBindCode())) {
                return ReErrorCode.DEVICE_NOT_BIND;
            }
            // 检查是否修改
            if (shadowBean.getDoc().getState().getDesired().isEmpty()) {
                return ReErrorCode.SHADOW_ATTR_NOT_MODIFIED;
            }
            // 更新版本
            shadowBean.getDoc().addUpVersion();
            // 下发
            CommParams commParams = new CommParams(shadowBean.getProtocol(), shadowBean.getIp(),
                    shadowBean.getTopic(), shadowBean.getEncryption(), shadowBean.getDeviceId());
            long current = shadowBean.getDoc().getTimestamp();
            controlPush.push(deviceId, commParams, shadowBean.getDoc(), current);
        }
        return null;
    }

    /**
     * 影子回滚
     *
     * @param deviceId 设备id
     *
     * @author szh
     * @since 2019/7/24 10:27
     */
    public static void revert(String deviceId) {
        ShadowBean bean = ShadowFactory.getShadowBean(deviceId);
        if (null != bean) {
            bean.shadowRevert();
        }
    }

    /**
     * 释放信号量
     *
     * @param deviceId 设备id
     */
    public static synchronized void releaseSemaphore(String deviceId) {
        Semaphore semaphore = ShadowFactory.getSemaphore(deviceId);
        semaphore.release();
        ShadowUtils.class.notifyAll();
    }

    /**
     * 通过sri获得实体
     *
     * @param sri 影子标识
     * @return 实体
     *
     * @author szh
     * @since 2019/8/9 23:41
     */
    @SuppressWarnings("unchecked")
    public static <T extends ShadowEntity> T getEntity(String sri) {
        return (T) EntityFactory.getEntity(sri);
    }

    /**
     * 增加实体
     *
     * @param entity 实体
     *
     * @author szh
     * @since 2019/8/14 13:44
     */
    public static void addEntity(ShadowEntity entity) {
        if (StringUtils.isEmpty(entity.getDeviceId())) {
            throw new NoDeviceIdException();
        }
        EntityFactory.injectEntities(entity, entity.getDeviceId(), entityNames);
    }

}
