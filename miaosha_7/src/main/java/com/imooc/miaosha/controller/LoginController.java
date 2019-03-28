package com.imooc.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.util.ValidatorUtil;
import com.imooc.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	MiaoshaUserService userService;
	@Autowired
    RedisService redisService;
	
	@RequestMapping("/to_login")
//    @ResponseBody
    String toLogin() {
        return "login";
    }

 	@RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response,@Valid LoginVo loginvo) {
 		log.info(loginvo.toString());
 		
// 		//参数校验（空+格式错误）,可以使用JSR完成参数校验
// 		String passInput = loginvo.getPassword();
// 		String mobile = loginvo.getMobile();
// 		if(StringUtils.isEmpty(passInput)) {
// 			return Result.error(CodeMsg.PASSWORD_EMPTY);
// 		}
// 		if(StringUtils.isEmpty(mobile)) {
// 			return Result.error(CodeMsg.MOBILE_EMPTY);
// 		}
// 		if(!ValidatorUtil.isMobile(mobile)) {
// 			return  Result.error(CodeMsg.MOBILE_ERROR);
// 		}
// 		
 		//调用MiaoshaUserService实现登录
 		String token = userService.login(response,loginvo);
// 		if(cm.getCode()==0) {
// 			return Result.success(true);
// 		}else {
// 			return Result.error(cm);
// 		}
 		return Result.success(token);
		
    }
	
}
