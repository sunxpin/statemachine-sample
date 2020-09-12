package com.baoxian.common.util;

public class SlaveUtil {
	protected static final ThreadLocal<Boolean> LOCAL_PAGE = new ThreadLocal<>();

	public static Boolean getSlave() {
		return LOCAL_PAGE.get();
	}

	public static void setSlave() {
		LOCAL_PAGE.set(true);
	}

	public static void clear() {
		LOCAL_PAGE.set(false);
	}

}
