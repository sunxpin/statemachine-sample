package com.baoxian.common.util;

import com.baoxian.common.annotation.LoggerRequest;

import java.util.HashMap;
import java.util.Map;

public class FeignUtil {
	private static Map<String, LoggerRequest> map = new HashMap<>();

	public static LoggerRequest get(String configKey) {
		return map.get(configKey);
	}

	public static void put(String configKey, LoggerRequest loggerRequest) {
		map.put(configKey, loggerRequest);
	}
}
