package com.cislc.shadow.manage.core.sync.keepAlive;

import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.cislc.shadow.manage.core.bean.shadow.ShadowBean;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备检查任务
 *
 * @author szh
 * @since 2020/12/25 14:21
 **/
@Conditional(ScheduledCondition.class)
@Component
@Slf4j
public class DeviceCheckTask implements SchedulingConfigurer {

    /** 超时时长，默认1分钟 **/
    @Value("${shadow.keepAlive.timeout:60000}")
    private long timeout;

    /** 任务执行周期，默认1分钟 **/
    @Value("${shadow.keepAlive.interval:60000}")
    private long interval;

    /** 超时处理策略 **/
    @Autowired(required = false)
    private DeviceLostStrategy strategy;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addFixedDelayTask(() -> {
            long current = System.currentTimeMillis();

            // 轮询所有设备
            List<ShadowEntity> lostDevice = new ArrayList<>();
            List<ShadowBean> shadowBeanList = ShadowFactory.getShadowBeans();
            for (ShadowBean bean : shadowBeanList) {
                if (bean.isBound() && current - bean.getLastUpdateTime() > timeout) {
                    // 已绑定且超时的设备
                    lostDevice.add(bean.getData());
                    log.warn("device {} has been lost.", bean.getDeviceId());
                }
            }

            // 执行处理策略
            if (!lostDevice.isEmpty() && strategy != null) {
                strategy.process(lostDevice);
            }
        }, interval);
    }
}
