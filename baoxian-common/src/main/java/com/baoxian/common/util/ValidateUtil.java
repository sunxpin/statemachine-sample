package com.baoxian.common.util;

public class ValidateUtil {
	public static boolean isMobile(String data) {
		return data.matches("^1\\d{10}$");
	}

	public static boolean isEamil(String data) {
		return data.matches("^.+@.+\\..+$");
	}

	public static boolean isInt(String data) {
		return data.matches("^\\d+$");
	}

	public static boolean isDouble(String data) {
		return data.matches("^\\d+\\.\\d*$");
	}

	public static boolean isIdno(String data) {
		return data.matches("^\\d{18}$");
	}

}
