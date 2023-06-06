package com.cislc.shadow.manage.core.sync.keepAlive;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 定时任务开启条件
 *
 * @author szh
 * @since 2020/12/25 16:21
 **/
public class ScheduledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return Boolean.parseBoolean(conditionContext.getEnvironment().getProperty("shadow.keepAlive.enable"));
    }

}
