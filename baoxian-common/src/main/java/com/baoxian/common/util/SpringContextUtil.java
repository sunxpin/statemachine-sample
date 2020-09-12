package com.baoxian.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.util.Map;

@Component
public class SpringContextUtil implements ApplicationContextAware, DisposableBean {
	private static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);
	private static ApplicationContext applicationContext = null;

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	public static boolean containsBean(String name) {
		assertContextInjected();
		return applicationContext.containsBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		Map<String, T> beanMaps = applicationContext.getBeansOfType(requiredType);
		if (beanMaps == null)
			return null;
		if (beanMaps.size() == 1) {
			return beanMaps.values().iterator().next();
		}
		if (beanMaps.containsKey(Introspector.decapitalize(requiredType.getSimpleName()))) {
			return beanMaps.get(Introspector.decapitalize(requiredType.getSimpleName()));
		}
		return null;
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext属性未注入, 请在applicationContext"
					+ ".xml中定义SpringContextHolder或在SpringBoot启动类中注册SpringContextHolder.");
		}
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		applicationContext = null;
	}

	@Override
	public void destroy() throws Exception {
		SpringContextUtil.clearHolder();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringContextUtil.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringContextUtil.applicationContext);
		}
		SpringContextUtil.applicationContext = applicationContext;
	}
}
