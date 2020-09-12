package com.baoxian.common.config;

import com.baoxian.common.annotation.LoggerRequest;
import com.baoxian.common.dao.BaseDao;
import com.baoxian.common.entity.BaseEntity;
import com.baoxian.common.services.BaseService;
import com.baoxian.common.statemachine.entity.StateMachineEntity;
import com.baoxian.common.statemachine.BaseStatemachineService;
import com.baoxian.common.util.BaoxianUtil;
import com.baoxian.common.util.DynamicCreateClassUtil;
import com.baoxian.common.util.FeignUtil;
import feign.Feign;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("rawtypes")
public class BeanRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(BeanRegistryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		Reflections f = new Reflections("com.baoxian");
		Set<Class<? extends BaseEntity>> entitys = f.getSubTypesOf(BaseEntity.class);
		Set<Class<? extends BaseDao>> daos = f.getSubTypesOf(BaseDao.class);
		Set<Class<?>> feignClients = f.getTypesAnnotatedWith(FeignClient.class);		
		
		Map<String, Class<?>> daoNameMap = daos.stream().collect(Collectors.toMap(Class::getName,
				e -> (Class<?>) ((ParameterizedType) e.getGenericInterfaces()[0]).getActualTypeArguments()[0]));
		Set<String> daoNameSet = daoNameMap.values().stream().map(Class::getName).collect(Collectors.toSet());
		Set<Class<? extends BaseService>> subTypesOf = f.getSubTypesOf(BaseService.class);

		Set<String> serviceNameSet = subTypesOf.stream()
				.filter(e -> !e.isAssignableFrom(BaseStatemachineService.class))
				.map(e -> (ParameterizedType) e.getGenericSuperclass())
				.map(e -> (Class<?>) e.getActualTypeArguments()[0])
				.map(Class::getName).collect(Collectors.toSet());

		List<Class<? extends BaseDao>> daoList = new ArrayList<>();
		List<Class<?>> serviceList = new ArrayList<Class<?>>();
		for (Class<?> entityClass : entitys) {
			if(entityClass.isAssignableFrom(StateMachineEntity.class)){
				continue;
			}
			if (!daoNameSet.contains(entityClass.getName())) {
				Class<? extends BaseDao<?>> daoClass = DynamicCreateClassUtil.createDaoClassByEntity(entityClass.getPackage().getName(), entityClass.getSimpleName());
				if (daoClass == null)
					continue;
				daoList.add(daoClass);
				RootBeanDefinition rbd = new RootBeanDefinition(daoClass);
				rbd.setBeanClass(MapperFactoryBean.class);
				rbd.setScope(AbstractBeanDefinition.SCOPE_SINGLETON);
				rbd.setLazyInit(false);
				rbd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
				rbd.getConstructorArgumentValues().addGenericArgumentValue(daoClass);
				registry.registerBeanDefinition(Introspector.decapitalize(daoClass.getSimpleName()), rbd);
				daoNameMap.put(daoClass.getName(), entityClass);
			}
			if (!serviceNameSet.contains(entityClass.getName())) {
				Class<?> serviceClass = DynamicCreateClassUtil
						.createServiceClassByEntity(entityClass.getPackage().getName(), entityClass.getSimpleName());
				if (serviceClass == null)
					continue;
				RootBeanDefinition rbd = new RootBeanDefinition(serviceClass);
				rbd.setScope(AbstractBeanDefinition.SCOPE_SINGLETON);
				rbd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
				registry.registerBeanDefinition(Introspector.decapitalize(serviceClass.getSimpleName()), rbd);
				serviceList.add(serviceClass);
			}
		}
		logger.info("Register Dao's BeanDefinition: " + daoList.toString());
		logger.info("Register Service's BeanDefinition: " + serviceList.toString());
		initFeignLoggerRequest(feignClients);
		BaoxianUtil.setDaoNameMap(daoNameMap);
	}
	
	private void initFeignLoggerRequest(Set<Class<?>> feignClients) {
		logger.info("Initializing Feign Client Config");
		for (Class<?> clz : feignClients) {
			LoggerRequest classloggerRequest = clz.getAnnotation(LoggerRequest.class);
			for (Method method : clz.getMethods()) {
				LoggerRequest methodloggerRequest = method.getAnnotation(LoggerRequest.class);
				FeignUtil.put(Feign.configKey(clz, method),
						methodloggerRequest == null ? classloggerRequest : methodloggerRequest);
			}
		}
	}
}
