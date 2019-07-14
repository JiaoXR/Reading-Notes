package com.jaxer.example.proxy;

/**
 * 代码千万行，注释第一行。
 * 实现类
 * <p>
 * Created by jaxer on 2019-07-13
 */
public class UserServiceImpl implements UserService {
	@Override
	public void save(String name) {
		System.out.println("保存用户：" + name);
	}
}
