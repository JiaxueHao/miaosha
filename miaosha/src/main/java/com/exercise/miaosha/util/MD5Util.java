package com.imooc.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}

	private static final String salt="1a2b3c4d";
	
	public static String inputPassToDBPass(String inputPass,String saltDB) {
		String temp = inputPassFormPass(inputPass);
		String pass = formPassToDBPass(temp,saltDB);
		return pass;
	}
	
	//客户端MD5，salt固定，否则服务器不知道密码	
	public static String inputPassFormPass(String inputPass) {
		String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
		
		return md5(str);
	}
	
	//服务端MD5，salt随机
	public static String formPassToDBPass(String inputPass,String salt) {
		
		String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);//12123456c3
		return md5(str);
	}
	
	public static void main(String[] args) {
//		System.out.println(inputPassFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9  第一次MD5
//		System.out.println(formPassToDBPass(inputPassFormPass("123456"),"1a2b3c4d")); //   第二次MD5
		System.out.println(inputPassToDBPass("123456","1a2b3c4d"));//经过两次MD5：b7797cce01b4b131b433b6acf4add449
		
	}
	
}
