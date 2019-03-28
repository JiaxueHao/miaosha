package com.imooc.miaosha.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

@Service
public class RedisService {
	@Autowired
	JedisPool jedisPool;
	//判断key是否存在
	public <T> boolean exists(KeyPrefix prefix,String key){
		Jedis jedis = null;		
		try {
			jedis=jedisPool.getResource();			
			String tempkey = prefix.getPrefix();
			String realKey = tempkey+key;
			return jedis.exists(realKey);
			//连接池要释放掉			
		}finally{
			returnToPool(jedis);
		}
	}
	//增加值（原子操作）：若key值不存在或者类型错误，把key赋值为0，再做加法；若key值正确，则返回value加1后的值
	public <T> Long incr(KeyPrefix prefix,String key){
		Jedis jedis = null;		
		try {
			jedis=jedisPool.getResource();
			
			String tempkey = prefix.getPrefix();
			String realKey = tempkey+key;
			return jedis.incr(realKey);
			//连接池要释放掉			
		}finally{
			returnToPool(jedis);
		}
	}
	//减少值（原子操作）：若key值不存在或者类型错误，把key赋值为0，再做减法；若key值正确，则返回value减1后的值
	public <T> Long decr(KeyPrefix prefix,String key){
		Jedis jedis = null;		
		try {
			jedis=jedisPool.getResource();
			
			String tempkey = prefix.getPrefix();
			String realKey = tempkey+key;
			return jedis.decr(realKey);
			//连接池要释放掉			
		}finally{
			returnToPool(jedis);
		}
	}
//获取单个对象，转化为clazz类
	public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
		Jedis jedis = null;		
		try {
			String tempkey = prefix.getPrefix();
			String realKey = tempkey+key;
			jedis=jedisPool.getResource();
			String str = jedis.get(realKey);
			T t = stringToBean(str,clazz);
			return t;
			//连接池要释放掉			
		}finally{
			returnToPool(jedis);
		}
	}
	//添加对象
	public <T> boolean set(KeyPrefix prefix,String key,T value){
		Jedis jedis = null;		
		try {
			String tempkey = prefix.getPrefix();
			String realKey = tempkey+key;
			jedis=jedisPool.getResource();
			String str =beanToString(value);
			if(str==null ||str.length()<=0) {
				return false;
			}
			int seconds = prefix.expireSeconds();
			if(seconds <=0 ) {
				jedis.set(realKey, str);
			}else {
				//设置过期值
				jedis.setex(realKey,seconds,str);
			}
		return true;
			//连接池要释放掉			
		}finally{
			returnToPool(jedis);
		}	
	}
	//删除对象
	public <T> Boolean delete(KeyPrefix prefix,String key){
		Jedis jedis = null;		
		try {
			jedis=jedisPool.getResource();
			
			String tempkey = prefix.getPrefix();
			String realKey = tempkey+key;
			long res = jedis.del(realKey);
			return res>0;
			//连接池要释放掉			
		}finally{
			returnToPool(jedis);
		}
	}
	private <T> String beanToString(T value) {
		// TODO Auto-generated method stub
		if(value==null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz==int.class||clazz==Integer.class) {
			return ""+value;
		}else if(clazz==String.class){
			return (String)value;
		}else if(clazz==Long.class ||clazz==long.class){
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}				
	}

	private<T> T stringToBean(String str,Class<T> clazz) {
		if(str==null||str.length()<=0||clazz==null) {
			return null;
		}
		if(clazz==int.class||clazz==Integer.class) {
			//parseInt返回int基本数据类型
			//valueOf返回Integer对象
			return (T)Integer.valueOf(str);
		}else if(clazz==String.class){
			return (T)str;
		}else if(clazz==Long.class ||clazz==long.class){
			return (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str),clazz);
		}		
	}

	private void returnToPool(Jedis jedis) {
		// TODO Auto-generated method stub
		if(jedis!=null) {
			jedis.close();
		}
	}
	public boolean delete(KeyPrefix prefix) {
		if(prefix == null) {
			return false;
		}
		List<String> keys = scanKeys(prefix.getPrefix());
		if(keys==null || keys.size() <= 0) {
			return true;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(keys.toArray(new String[0]));
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public List<String> scanKeys(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			List<String> keys = new ArrayList<String>();
			String cursor = "0";
			ScanParams sp = new ScanParams();
			sp.match("*"+key+"*");
			sp.count(100);
			do{
				ScanResult<String> ret = jedis.scan(cursor, sp);
				List<String> result = ret.getResult();
				if(result!=null && result.size() > 0){
					keys.addAll(result);
				}
				//再处理cursor
				cursor = ret.getStringCursor();
			}while(!cursor.equals("0"));
			return keys;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
