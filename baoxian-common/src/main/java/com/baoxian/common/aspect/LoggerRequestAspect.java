package com.baoxian.common.aspect;

import com.baoxian.common.annotation.LoggerRequest;
import com.baoxian.common.util.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;

/**
 * @author 李德英
 */
@Aspect
@Configuration
public class LoggerRequestAspect {
	private static Logger logger = LoggerFactory.getLogger(LoggerRequestAspect.class);
	private static ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	@Pointcut("execution(* com.baoxian..*Controller..*(..)))")
	public void excudeService() {
	}

	@Around("excudeService()")
	public Object doAround(ProceedingJoinPoint point) throws Throwable {
		HttpServletRequest request = HttpUtil.getRequest();
		
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		Signature signature = point.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method targetMethod = methodSignature.getMethod();
		Object[] args = point.getArgs();
		LoggerRequest loggerRequest = targetMethod.getAnnotation(LoggerRequest.class);
		if (loggerRequest == null)
			loggerRequest = point.getTarget().getClass().getAnnotation(LoggerRequest.class);
		if (loggerRequest == null || loggerRequest.urlLogger()) {
			if (queryString == null || "".equals(queryString))
				logger.info("请求地址: " + request.getMethod() + " " + uri);
			else
				logger.info("请求地址: " + request.getMethod() + " " + uri + "?" + queryString);
		}
		if (loggerRequest == null || loggerRequest.ipLogger()) 
			logger.info("请求IP: " + HttpUtil.getIpAddr());
		if (loggerRequest == null || (loggerRequest.bodyLogger() && args != null)) {
			Parameter[] parameters = targetMethod.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
				if (requestBody != null) {
					if(args[i] instanceof String)
						logger.info("请求内容: " + ((String)args[i]).replaceAll("[\r\n\t]", ""));
					else
						logger.info("请求内容: " + mapper.writeValueAsString(args[i]).replaceAll("[\r\n\t]", ""));
					break;
				}
			}
		}
		long s = System.currentTimeMillis();
		Object obj = point.proceed();
		long e = System.currentTimeMillis();
		if (loggerRequest == null || loggerRequest.returnLogger()) {
			if(obj instanceof String)
				logger.info("请求耗时: " + (e - s) + "ms, 返回结果: " + ((String)obj).replaceAll("[\r\n\t]", ""));
			else
			logger.info("请求耗时: " + (e - s) + "ms, 返回结果: " + mapper.writeValueAsString(obj).replaceAll("[\r\n\t]", ""));
		} else {
			logger.info("请求耗时: " + (e - s) + "ms");
		}
		return obj;
	}
}
