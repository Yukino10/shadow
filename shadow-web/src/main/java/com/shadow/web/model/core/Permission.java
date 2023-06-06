package com.shadow.web.model.core;

import java.lang.annotation.*;

/**
 * controller层中的带@RequestMapping等注解的api方法，
 * 加上此注解来控制访问权限
 * value值为权限的name，即cm_permission表的permissionName值
 * @author Administrator
 *
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
	
	String value() default "";

}
