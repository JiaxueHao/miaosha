package com.imooc.miaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.imooc.miaosha.domain.User;


@Mapper
public interface UserDao {
	//不需要配置文件的方法
	@Select("select * from user where id=#{id}")
	public User getById(@Param("id")int id);

	@Insert("insert into user values(#{id},#{name})")
	public int insert(User user);

}
