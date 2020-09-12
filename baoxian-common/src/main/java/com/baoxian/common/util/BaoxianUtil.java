package com.baoxian.common.util;

import java.util.HashMap;
import java.util.Map;

public class BaoxianUtil {
	private static Map<String, Class<?>> daoNameMap = new HashMap<String, Class<?>>();

	public static Map<String, Class<?>> getDaoNameMap() {
		return daoNameMap;
	}

	public static void setDaoNameMap(Map<String, Class<?>> daoNameMap) {
		BaoxianUtil.daoNameMap = daoNameMap;
	}

}
