package com.baoxian.common.util;

import java.util.concurrent.ConcurrentHashMap;

public class ProgressUtil {
	private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
	
	public static void put(String taskId, String info){
		map.put(taskId, info);
	}
	
	public static String get(String taskId){
		return map.get(taskId);
	}
	
	public static void finish(String taskId){
		map.remove(taskId);
	}
}
