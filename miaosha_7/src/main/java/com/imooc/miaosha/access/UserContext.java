package com.imooc.miaosha.access;

import com.imooc.miaosha.domain.MiaoshaUser;

public class UserContext {
	//多线程时，保证线程安全
	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();//将用户信息保存到threadlocal中
	
	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	}

}
