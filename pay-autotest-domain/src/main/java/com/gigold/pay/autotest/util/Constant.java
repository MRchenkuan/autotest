package com.gigold.pay.autotest.util;

import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;

public class Constant {

	
	public static String SYSTEMPARAM_PAGESIZE="sys.pageSize";
	public static int PAGE_SIZE=20;
	public static String LOGIN_KEY = SystemPropertyConfigure.getProperty("loginkey");
	public static String SESSION_VALUE_GIGOLD_AUTH = "true";
}
