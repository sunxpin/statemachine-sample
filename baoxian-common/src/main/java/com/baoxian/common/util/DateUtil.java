package com.baoxian.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
//	private static Pattern dayPattern = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
//	private static Pattern datePattern = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$");
//	private static Pattern timePattern = Pattern.compile("^[0-9]{2}:[0-9]{2}:[0-9]{2}$");
	private static String dayPattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";
	private static String datePattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$";
	private static String timePattern = "^[0-9]{2}:[0-9]{2}:[0-9]{2}$";

	public static Date dayParse(String day) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(day);
		} catch (Exception e) {
			logger.error("解析日期出现错误: " + day, e);
		}
		return null;
	}

	public static Date dateParse(String date) {
		try {
			if (date == null)
				return null;
			if (date.matches(datePattern))
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
			if (date.matches(dayPattern))
				return new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (Exception e) {
			logger.error("解析日期出现错误: " + date, e);
		}
		return null;
	}

	public static Date timeParse(String date) {
		try {
			if (date == null)
				return null;
			if (date.matches(timePattern))
				return new SimpleDateFormat("hh:mm:ss").parse(date);
		} catch (Exception e) {
			logger.error("解析日期出现错误: " + date, e);
		}
		return null;
	}

	public static String dayFormat(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static String dateFormat(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public static String timeFormat(Date date) {
		return new SimpleDateFormat("hh:mm:ss").format(date);
	}

	public static String getTimestamp(Date date) {
		if (date != null)
			return String.valueOf(date.getTime());
		else
			return null;
	}
	
	/**
	 * 比较两个日期类型相隔的毫秒数
	 * 
	 * @param start 要比较的第一个日期
	 * @param end   要比较的第二个日期
	 * @return long,大于.0则end时间在start之后，反之
	 */
	public static long compare(Date start, Date end) {
		long mills = end.getTime() - start.getTime();
		return mills;
	}
}
