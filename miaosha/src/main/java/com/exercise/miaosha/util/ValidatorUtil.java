package com.imooc.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//检查手机号格式是否正确

public class ValidatorUtil {
	//如果1后面为10个数字则为正确
	private static final Pattern mobile_pattern=Pattern.compile("1\\d{10}");
	
	public static boolean isMobile(String str) {
		Matcher m = mobile_pattern.matcher(str);
		return m.matches();
	}

}
