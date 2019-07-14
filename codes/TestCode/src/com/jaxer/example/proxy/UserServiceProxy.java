package com.jaxer.example.proxy;

/**
 * 代码千万行，注释第一行。
 * 静态代理类
 * <p>
 * Created by jaxer on 2019-07-13
 */
public class UserServiceProxy implements UserService {
	private UserService userService = new UserServiceImpl();

	@Override
	public void save(String name) {
		System.out.println("---静态代理类---");
		userService.save(name);
		System.out.println("---静态代理类---");
	}
}
