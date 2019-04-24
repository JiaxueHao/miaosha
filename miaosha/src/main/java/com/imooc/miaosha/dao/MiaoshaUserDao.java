package com.imooc.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.imooc.miaosha.domain.MiaoshaUser;

@Mapper
public interface MiaoshaUserDao {
	
	@Select("select * from miaosha_user where id = #{id}")
	public MiaoshaUser getById(@Param("id")long id);
//只更新pass，只传入pass，防止二进制日志太多
	@Update("update miaosha_user set password = #{password} where id=#{id}")
	public void update(MiaoshaUser toBeUpdate);
}
