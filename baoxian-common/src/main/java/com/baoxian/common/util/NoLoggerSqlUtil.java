package com.baoxian.common.util;

public class NoLoggerSqlUtil {
	protected static final ThreadLocal<Boolean> LOCAL_LOGGER = new ThreadLocal<>();

	public static Boolean getNoLogger() {
		return LOCAL_LOGGER.get();
	}

	public static void setNoLogger(Boolean logger) {
		LOCAL_LOGGER.set(logger);
	}

	public static void clearNoLogger() {
		LOCAL_LOGGER.remove();
	}

}
