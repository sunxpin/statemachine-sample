package com.baoxian.common.util;

import java.util.UUID;

public class UUIDTools {
	public static String getUUID() {  
        return UUID.randomUUID().toString().replace("-", "");  
    } 
}
