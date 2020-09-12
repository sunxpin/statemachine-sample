package com.baoxian.common.aspect;

import com.baoxian.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 李德英
 */
@ControllerAdvice
public class WebExceptionAspect {
	private static Logger logger = LoggerFactory.getLogger(WebExceptionAspect.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public String handleHttpMessageNotReadableException(HttpServletRequest request, HttpServletResponse response,
			HttpMessageNotReadableException e) {
		return handler(request, response, e, "400", "错误的请求");
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public String handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpServletResponse response,
			HttpRequestMethodNotSupportedException e) {
		return handler(request, response, e, "405", "不支持的请求方法");
	}

	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public String handleHttpMediaTypeNotSupportedException(HttpServletRequest request, HttpServletResponse response,
			Exception e) {
		return handler(request, response, e, "415", "不支持的媒体类型");
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public String handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		return handler(request, response, e, "500", "内部服务错误");
	}

	private String handler(HttpServletRequest request, HttpServletResponse response, Exception e, String errorCode,
			String errorMsg) {
		logger.error(errorMsg, e);
		request.setAttribute("code", errorCode);
		request.setAttribute("msg", errorMsg);
		if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))
				|| StringUtils.isNotBlank(request.getHeader("x-b3-traceid"))) {
			StringBuilder builder = new StringBuilder();
			builder.append("{\"code\":\"" + errorCode + "\",");
			builder.append("\"msg\":\"" + errorMsg + ": " + e.getMessage() + "\"}");
			response.addHeader("Content-Type", "application/json;charset=UTF-8");
			HttpUtil.writeAndClose(builder.toString());
			return null;
		}
		return "/error";
	}
}