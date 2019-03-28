package com.imooc.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {
	
	
	public static final String COOKI_NAME_TOKEN = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	//通过用户ID获取用户+对象缓存
	public MiaoshaUser getById(long id) {
		//取缓存		
		MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
		if(user!=null) {
			return user;
		}
		//没有缓存，从数据库中查询
		user = miaoshaUserDao.getById(id);
		if(user!=null) {
			//数据保存到redis缓存中
			redisService.set(MiaoshaUserKey.getById, ""+id, user);
		}
		return user;
	}
	
	//更新用户密码
	public boolean updatePassword(String token,long id,String passwordNew) {
		//取user
		MiaoshaUser user = getById(id);
		if(user==null) {
			throw new GlobalException(CodeMsg.USER_NOT_EXISTS);
		}
		//更新数据库
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(passwordNew, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//更新缓存
		redisService.delete(MiaoshaUserKey.getById, ""+id);
		user.setPassword(toBeUpdate.getPassword());
		//token不能删除，删除后无法登陆，必须更新
		redisService.set(MiaoshaUserKey.token,token,user);
		return true;
		
	}
	

	public String login(HttpServletResponse response,LoginVo loginvo) {
		// TODO Auto-generated method stub
		//loginvo从登陆页面得到输入的user信息
		if(loginvo==null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile  = loginvo.getMobile();
		String password  = loginvo.getPassword();			//客户端输入的密码，经过一次MD5
		MiaoshaUser user = getById(Long.parseLong(mobile));		
		
		//判断用户是否存在
		if(user==null) {
			throw new GlobalException(CodeMsg.USER_NOT_EXISTS);
		}
		
		//验证密码
		String dbPass = user.getPassword();					//服务端保存的密码，经过两次MD5
		String dbsalt = user.getSalt();
		String clacpass = MD5Util.formPassToDBPass(password,dbsalt);
		if(!clacpass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		
		//登陆成功				
		//生成cookie并返回
		String token = UUIDUtil.uuid();
		addCookie(response,token,user);
		return token;
		
	}
	//将私人信息缓存到第三方缓存redis中,key为token，value为user
	public void addCookie(HttpServletResponse response,String token, MiaoshaUser user) {		
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		//设置有效期
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public MiaoshaUser getByToken(HttpServletResponse response,String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}		
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//延长有效期,当第二次访问以后，cookie有效期更新,更新设置cookie
		if(user!= null) {
			addCookie(response,token,user);
		}
		return user;
	}
}
