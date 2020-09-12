package com.baoxian.common.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CastUtil {
	public static List<Class<?>> primitiveClass = new ArrayList<>();

	static {
		primitiveClass.add(Integer.class);
		primitiveClass.add(int.class);
		primitiveClass.add(Long.class);
		primitiveClass.add(long.class);
		primitiveClass.add(Double.class);
		primitiveClass.add(double.class);
		primitiveClass.add(Float.class);
		primitiveClass.add(float.class);
		primitiveClass.add(String.class);
		primitiveClass.add(Short.class);
		primitiveClass.add(short.class);
		primitiveClass.add(Date.class);
		primitiveClass.add(BigDecimal.class);
		primitiveClass.add(Boolean.class);
		primitiveClass.add(boolean.class);
		primitiveClass.add(Byte.class);
		primitiveClass.add(byte.class);
		primitiveClass.add(Object.class);
	}

	public static Object cast(Object value, Class<?> cls) {
		if (value == null)
			return null;
		if (cls == Integer.class || cls == int.class)
			return castToInteger(value);
		if (cls == Long.class || cls == long.class)
			return castToLong(value);
		if (cls == Float.class || cls == float.class)
			return castToFloat(value);
		if (cls == Double.class || cls == double.class)
			return castToDouble(value);
		if (cls == BigDecimal.class)
			return castToBigDecimal(value);
		if (cls == String.class)
			return castToString(value);
		if (cls == Date.class)
			return castToDate(value);
		return value;
	}

	public static Integer castToInteger(Object value) {
		return Integer.parseInt(value.toString());
	}

	public static Long castToLong(Object value) {
		return Long.parseLong(value.toString());
	}

	public static Float castToFloat(Object value) {
		return Float.parseFloat(value.toString());
	}

	public static Double castToDouble(Object value) {
		return Double.parseDouble(value.toString());
	}

	public static BigDecimal castToBigDecimal(Object value) {
		return new BigDecimal(value.toString());
	}

	public static Boolean castToBoolean(Object value) {
		return Boolean.parseBoolean(value.toString());
	}

	public static String castToString(Object value) {
		if (value instanceof Date)
			return DateUtil.dateFormat((Date) value);
		return value.toString();
	}

	public static Date castToDate(Object value) {
		if (value instanceof Date || value instanceof Timestamp)
			return (Date) value;
		return DateUtil.dateParse(value.toString());
	}

	public static boolean isPrimitiveType(Class<?> clz) {
		return primitiveClass.contains(clz);
	}
}
