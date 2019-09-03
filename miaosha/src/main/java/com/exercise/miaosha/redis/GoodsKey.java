package com.imooc.miaosha.redis;

public class GoodsKey extends BasePrefix {
	public static final int TOKEN_EXPIRE=60;
	private GoodsKey(int expireSeconds,String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}
	
	public static GoodsKey getGoodsList = new GoodsKey(TOKEN_EXPIRE,"gl");
	public static GoodsKey getGoodsDetail = new GoodsKey(TOKEN_EXPIRE,"gd");
	public static GoodsKey getMiaoGoodsStock = new GoodsKey(0,"gs");
}
