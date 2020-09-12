package com.baoxian.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class HttpUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
				.getRequest();
		return request;
	}

	public static Map<String, String> getParameters() {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> e = HttpUtil.getRequest().getParameterNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			map.put(name, HttpUtil.getRequest().getParameter(name));
		}
		return map;
	}

	public static Map<String, String[]> getParameterMap() {
		return HttpUtil.getRequest().getParameterMap();
	}

	public static String[] getParameterValues(String name) {
		return HttpUtil.getRequest().getParameterValues(name);
	}

	public static ArrayList<String> getParameterNames(String name) {
		return Collections.list(HttpUtil.getRequest().getParameterNames());
	}

	public static String getParameter(String parameter) {
		return getRequest().getParameter(parameter);
	}

	public static HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
				.getResponse();
		return response;
	}

	public static void setParameter(String key, Object parameter) {
		getRequest().setAttribute(key, parameter);
	}
	
	public static void removeAttribute(String key) {
		getRequest().removeAttribute(key);
	}

	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static Object getSession(String parameter) {
		return getRequest().getSession().getAttribute(parameter);
	}
	
	public static void removeSession(String parameter) {
		getRequest().getSession().removeAttribute(parameter);
	}

	public static void setSession(String key, Object parameter) {
		getRequest().getSession().setAttribute(key, parameter);
	}

	public static void write(Object content, String contentType, String charset) {
		try {
			HttpServletResponse response = getResponse();
			response.addHeader("Content-Type", contentType + ";charset=" + charset);
			PrintWriter writer = response.getWriter();
			if (content instanceof String)
				writer.write((String) content);
			else
				writer.write(new ObjectMapper().writeValueAsString(content));
			writer.flush();
		} catch (IOException ex) {
			logger.error("打开输出流异常", ex);
		}
	}

	public static void writeAndClose(Object content, String contentType, String charset) {
		try {
			HttpServletResponse response = getResponse();
			response.addHeader("Content-Type", contentType + ";charset=" + charset);
			PrintWriter writer = response.getWriter();
			if (content instanceof String)
				writer.write((String) content);
			else
				writer.write(new ObjectMapper().writeValueAsString(content));
			writer.flush();
			writer.close();
		} catch (IOException ex) {
			logger.error("打开输出流异常", ex);
		}
	}

	public static void write(Object content) {
		write(content, "application/json", "UTF-8");
	}

	public static void writeAndClose(Object content) {
		writeAndClose(content, "application/json", "UTF-8");
	}

	public static void write(String content, String contentType) {
		write(content, contentType, "UTF-8");
	}

	public static void writeAndClose(String content, String contentType) {
		writeAndClose(content, contentType, "UTF-8");
	}

	public static String getIpAddr() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0];
			}
			return ip;
		}
		ip = request.getHeader("Proxy-Client-IP");
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
			return ip;
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
			return ip;
		ip = request.getHeader("HTTP_CLIENT_IP");
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
			return ip;
		ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
			return ip;
		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
			return ip;
		ip = request.getRemoteAddr();
		if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip))
			return ip;
		return null;
	}

}
