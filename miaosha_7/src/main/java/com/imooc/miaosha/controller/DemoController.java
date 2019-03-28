package com.imooc.miaosha.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class DemoController {
		
	@Autowired
		UserService userService;
	@Autowired
	    RedisService redisService;
		
	 	@RequestMapping("/")
	    @ResponseBody
	    String home() {
	        return "Hello World!";
	    }
	 	//输出形式
	 	//1.rest api json输出
	 	@RequestMapping("/hello")
	    @ResponseBody
	    public Result<String> hello() {
	 		return Result.success("hello,imooc");
	       // return new Result(0, "success", "hello,imooc");
	    }
	 	
	 	@RequestMapping("/helloError")
	    @ResponseBody
	    public Result<String> helloError() {
	 		return Result.error(CodeMsg.SERVER_ERROR);
	 		//return new Result(500102, "XXX");
	    }
	 	
	 	//2.页面
	 	//返回对象为String 模板引擎
	 	@RequestMapping("/hello/thymeleaf")
	    public String  thymeleaf(Model model) {
	 		model.addAttribute("name", "Joshua");
	 		//浏览器标题为hello
	 		return "hello";
	    }
//查询Mysql数据库	 	
	 	@RequestMapping("/db/get")
	 	@ResponseBody
	    public Result<User>  dbGet() {
	 		User user = userService.getById(1);
	 		if(user==null) {
	 			return Result.error(CodeMsg.SERVER_ERROR);
	 		}else {
	 			return Result.success(user);
	 		}	 		
	    }
//测试事务插入数据	 	
	 	@RequestMapping("/db/tx")
	 	@ResponseBody
	    public Result<Boolean>  dbTx() {
	 		userService.Tx();
	 		return Result.success(true);	
	    }
	 	
	 	//查询数据库	 	
	 	@RequestMapping("/redis/get")
	 	@ResponseBody
	    public Result<User>  redisGet() {
	 		User u1=redisService.get(UserKey.getById,"key1",User.class);
	 		return Result.success(u1); 		
	    }
	 	
	 	//查询数据库	 	
	 	@RequestMapping("/redis/set")
	 	@ResponseBody
	    public Result<User>  redisSet() {
	 		User user = new User();
	 		user.setId(1);
	 		user.setName("Iris");
	 		//不同模块key不同
	 		Boolean v1=redisService.set(UserKey.getById,"key2",user);
	 		User str=redisService.get(UserKey.getById,"key2",User.class);
	 		return Result.success(str); 		
	    }
	 	
}
