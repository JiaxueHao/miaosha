package com.imooc.miaosha.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.miaosha.dao.UserDao;
import com.imooc.miaosha.domain.User;

@Service
public class UserService {
	@Autowired
	UserDao userDao;
	public User getById(int id) {
		return userDao.getById(id);
		
		
	}
	@Transactional  //事务标签，加上以后要么全执行，要么全回滚
	public boolean Tx() {
		// TODO Auto-generated method stub
		User u1=new User();
		u1.setId(2);
		u1.setName("Bob");		
		userDao.insert(u1);
		
		User u2=new User();
		u2.setId(1);
		u2.setName("Jack");		
		userDao.insert(u2);
		
		return true;
	}

}
