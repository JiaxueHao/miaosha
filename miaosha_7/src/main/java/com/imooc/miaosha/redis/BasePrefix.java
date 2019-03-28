package com.imooc.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix{

	private int expireSeconds;
	private String prefix;
	
	public BasePrefix(String prefix) {
		this(0, prefix);
		// TODO Auto-generated constructor stub
	}
	
	public BasePrefix(int expireSeconds,String prefix) {
		this.expireSeconds=expireSeconds;
		this.prefix=prefix;
	}
	
	//过期时间，0表示永不过期
	@Override
	public int expireSeconds() {
		// TODO Auto-generated method stub
		return expireSeconds;
	}
	//获取prefix
	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		String className = getClass().getSimpleName();
		return className+"."+prefix;
	}

}
