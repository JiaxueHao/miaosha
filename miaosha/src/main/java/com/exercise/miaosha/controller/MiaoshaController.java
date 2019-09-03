package com.imooc.miaosha.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.access.AccessLimit;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.OrderKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
	
	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	//redis中的内存标记
	private Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList =  goodsService.listGoodsVo();
		if(goodsList==null) {
			return;
		}
		for(GoodsVo goods:goodsList) {
			redisService.set(GoodsKey.getMiaoGoodsStock,""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}
	
	@RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getMiaoGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		//删缓存
		redisService.delete(OrderKey.getMiaoShaOrderByUidGid);
		redisService.delete(MiaoshaKey.isGoodsOver);
		//数据库删订单，置库存
		miaoshaService.reset(goodsList);
		return Result.success(true);
	}
	
	//页面静态化
	@RequestMapping(value="/{path}/do_miaosha",method=RequestMethod.POST)
	@ResponseBody
	public Result<OrderInfo> list(Model model,MiaoshaUser user,
			@RequestParam("goodsId")long goodsId,
			@PathVariable("path") String path) {
			model.addAttribute("user", user);
			//用户是否登陆
			if(user==null) {
				return Result.error(CodeMsg.SESSION_ERROR);
			}
			//验证path
	    	boolean check = miaoshaService.checkPath(user, goodsId, path);
	    	if(!check){
	    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
	    	}
	    	
			//内存标记，减少redis访问
			boolean over = localOverMap.get(goodsId);
			if(over) {
				return Result.error(CodeMsg.STOCK_ERROR);
			}
			
		    //预减库存（redis）
			long stock = redisService.decr(GoodsKey.getMiaoGoodsStock,""+goodsId);//即使库存已为0，后续仍需要进行decr
			if(stock<0) {
				localOverMap.put(goodsId,true);
				return Result.error(CodeMsg.STOCK_ERROR);
			}
			//判断是否秒杀到了(避免重复下单)（redis）
			MiaoshaOrder dborder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
			if(dborder!=null) {
				return Result.error(CodeMsg.REPEATE_ERROR);
				}	
			
/*
			//判断是否秒杀到了(避免重复下单)
			MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
			if(order!=null) {
				return Result.error(CodeMsg.REPEATE_ERROR);
				}
			*/
	      //进入数据库，判断真正的库存
			GoodsVo good = goodsService.getGoodsVoByGoodsId(goodsId);
			int dbstock = good.getStockCount();
			if(dbstock<=0) {
				model.addAttribute("errmsg",new GlobalException(CodeMsg.STOCK_ERROR));
				return Result.error(CodeMsg.STOCK_ERROR);
			}
		
			//减库存   下订单  写入秒杀订单（事务）
			OrderInfo orderInfo = miaoshaService.miaosha(user,good);
			return Result.success(orderInfo);
	}
	
	
	//消息队列中的客户端轮询，返回秒杀结果
	
	@RequestMapping(value="/result",method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> MiaoshaResult(Model model,MiaoshaUser user,
			@RequestParam("goodsId")long goodsId) {
			model.addAttribute("user", user);
			//用户是否登陆
			if(user==null) {
				return Result.error(CodeMsg.SESSION_ERROR);
			}
			//判断用户是否秒杀到商品,返回result
			//orderId：成功
			//-1：秒杀失败
			//0：排队中
			long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
			return Result.success(result);
	}
	
	//生成秒杀地址
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)			//接口限流（拦截器）：查询访问的次数，5秒访问5次
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId,
    		@RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    		) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	
    	//验证验证码
    	boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
    	if(!check) {
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    	}
    	//生成秒杀地址
    	String path  =miaoshaService.createMiaoshaPath(user, goodsId);
    	return Result.success(path);
    }
    
    //生成验证码
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	try {
    		BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
    		OutputStream out = response.getOutputStream();
    		ImageIO.write(image, "JPEG", out);
    		out.flush();
    		out.close();
    		return null;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return Result.error(CodeMsg.MIAOSHA_FAIL);
    	}
    }
    
    
}


