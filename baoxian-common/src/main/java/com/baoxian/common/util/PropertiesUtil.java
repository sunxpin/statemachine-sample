package com.baoxian.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static final Properties properties = new Properties();
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	static {
		try {
			logger.info("path: " + PropertiesUtil.class.getResource(".").toURI().toString());
			InputStream ins = PropertiesUtil.class.getResourceAsStream("/application.properties");
			if (ins == null) {
				ins = PropertiesUtil.class.getResourceAsStream("/application.yml");
			}
			properties.load(ins);
			ins.close();
		} catch (Exception e) {
			logger.error("加载配置文件出错", e);
		}
	}
	
	public static String get(String key){
		if(key == null) return null;
		return properties.getProperty(key);
	}
}
