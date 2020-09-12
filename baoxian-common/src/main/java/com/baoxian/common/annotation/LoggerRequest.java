package com.baoxian.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录日志注解类
 * @author 李德英
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggerRequest {

	/**
	 * 是否记录返回结果
	 * @return
	 */
	boolean returnLogger() default true;
	
	/**
	 * 是否记录消息体
	 * @return
	 */
	boolean bodyLogger() default true;
	
	/**
	 * 是否记录请求URL(含查询参数)
	 * @return
	 */
	boolean urlLogger() default true;
	
	/**
	 * 是否记录请求IP
	 * @return
	 */
	boolean ipLogger() default true;
}
