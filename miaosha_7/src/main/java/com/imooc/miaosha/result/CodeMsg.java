package com.imooc.miaosha.result;

public class CodeMsg {


	private int code;
	private String msg;
	//所有CodeMSG可在一个地方定义，而不至于零散在其他程序中
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	//通用异常	
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "绑定异常：%s");
	public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "非法请求");
	public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500503, "频繁访问");
	//登录模块 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或已失效");
//	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "password不能为空");
//	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "mobile不能为空");
//	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "mobile格式错误");
	public static CodeMsg USER_NOT_EXISTS = new CodeMsg(500214, "用户不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "用户密码错误");
	//商品模块 5003XX
	
	//订单模块 5004XX
	public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");
	//秒杀模块 5005XX
	public static CodeMsg STOCK_ERROR = new CodeMsg(500500, "商品库存不足");
	public static CodeMsg REPEATE_ERROR = new CodeMsg(500501, "不能重复秒杀");
	public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "秒杀失败");
	
	
	public CodeMsg fillArgs(Object...  args) {
		int code = this.code;
		String message = String.format(this.msg, args);//填充参数后的message
		return new CodeMsg(code,message);
	}
	
	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}
