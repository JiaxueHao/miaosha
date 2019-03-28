package com.imooc.miaosha.redis;

public class OrderKey extends BasePrefix {

	public OrderKey(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}
	
	public static OrderKey getMiaoShaOrderByUidGid = new OrderKey("moug");

}
