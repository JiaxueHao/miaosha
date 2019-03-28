package com.imooc.miaosha.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	//拦截所有异常
	@ExceptionHandler(value=Exception.class)
	public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
		e.printStackTrace();
		//SERVER_ERROR  USER_NOT_EXISTS  PASSWORD_ERROR
		if(e instanceof GlobalException){
			GlobalException ex = (GlobalException)e;
			return Result.error(ex.getCm());
		}
		//若是绑定异常BIND_ERROR，手机格式不正确
		else if(e instanceof BindException) {
			BindException ex = (BindException)e;
			
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		}else {
			//其他异常
			return Result.error(CodeMsg.SERVER_ERROR);
		}
	} 

}
