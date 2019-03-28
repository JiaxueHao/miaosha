package com.imooc.miaosha.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	//springboot框架中
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	@Autowired
	ApplicationContext applicationContext;
	//商品列表
//  第一次未优化压测  QPS:460    1000*5
	
	//页面缓存	
	@RequestMapping(value="/to_list",produces="text/html")
	@ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user){  	
    	model.addAttribute("user", user);
    	//取缓存
    	String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
    	if(!StringUtils.isEmpty(html)) {
    		return html;
    	}
    	//查询商品列表
    	List<GoodsVo> goodsList = goodsService.listGoodsVo();
    	model.addAttribute("goodsList", goodsList);    	
//    	return "goods_list";//使用springboot自动渲染模板
    	SpringWebContext ctx = new SpringWebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
    	//手动渲染
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
    	if(!StringUtils.isEmpty(html)) {
    		redisService.set(GoodsKey.getGoodsList, "", html);
    	}
    	return html;
    }
   
	//url缓存，带参数，不同参数不同页面
    //商品详情页,前端返回goodsId
    @RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
	@ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response,Model model,MiaoshaUser user,
    		@PathVariable("goodsId")long goodsId){  	
    	//一般不使用id，否则容易被遍历整个数据库中的信息，snowflake算法
    	model.addAttribute("user", user);
    	//取缓存
    	String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
    	if(!StringUtils.isEmpty(html)) {
    		return html;
    	}
    	//手动渲染模板
    	GoodsVo good = goodsService.getGoodsVoByGoodsId(goodsId);
    	model.addAttribute("goods", good);
    	
    	Long startAt = good.getStartDate().getTime();
    	Long endAt = good.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshastatus = 0;
    	int remainsecond = 0;
    	if(now<startAt)//秒杀未开始
    	{
    		miaoshastatus = 0;
    		remainsecond = (int) ((startAt-now)/1000);
    	}else if(now > endAt)//秒杀结束
    	{
    		miaoshastatus = 2;
    		remainsecond = -1;
    	}else//秒杀进行中 
    	{
    		miaoshastatus = 1;
    		remainsecond = 0;
    	}    	
    	model.addAttribute("miaoshastatus", miaoshastatus);
    	model.addAttribute("remainSeconds", remainsecond);
//    	return "goods_detail";
    	SpringWebContext ctx = new SpringWebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
    	if(!StringUtils.isEmpty(html)) {
    		redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
    	}
    	return html;
    }
    
    //页面静态化
    @RequestMapping(value="/detail/{goodsId}")
	@ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,Model model,MiaoshaUser user,
    		@PathVariable("goodsId")long goodsId){  	
    	
    	GoodsVo good = goodsService.getGoodsVoByGoodsId(goodsId);   	
    	Long startAt = good.getStartDate().getTime();
    	Long endAt = good.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshastatus = 0;
    	int remainsecond = 0;
    	if(now<startAt)//秒杀未开始
    	{
    		miaoshastatus = 0;
    		remainsecond = (int) ((startAt-now)/1000);
    	}else if(now > endAt)//秒杀结束
    	{
    		miaoshastatus = 2;
    		remainsecond = -1;
    	}else//秒杀进行中 
    	{
    		miaoshastatus = 1;
    		remainsecond = 0;
    	}    
    	
    	GoodsDetailVo vo = new GoodsDetailVo();
    	vo.setGoods(good);
    	vo.setUser(user);
    	vo.setMiaoshaStatus(miaoshastatus);
    	vo.setRemainSeconds(remainsecond);
    	return Result.success(vo);
    	
    	
    }
    
}