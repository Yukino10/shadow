package com.cislc.shadow.queue.queue.message;

import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgHandler {

    Class<? extends ShadowEntity> targetClass();

    String targetField() default "";

}
