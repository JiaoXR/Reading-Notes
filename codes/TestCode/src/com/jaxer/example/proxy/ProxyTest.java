package com.jaxer.example.proxy;

import com.jaxer.example.proxy.cglib.CglibProxyFactory;
import com.jaxer.example.proxy.jdk.JDKProxyFactory;

/**
 * 代码千万行，注释第一行。
 * 代理模式测试
 * <p>
 * Created by jaxer on 2019-07-13
 */
public class ProxyTest {
	public static void main(String[] args) {
		testStaticProxy();

//		testJdkProxy();

//		testCglibProxy();
	}

	/**
	 * Cglib代理
	 */
	private static void testCglibProxy() {
		UserService userService = new UserServiceImpl();
		CglibProxyFactory cglibProxyFactory = new CglibProxyFactory();
		UserService proxy = (UserService) cglibProxyFactory.getProxy(userService.getClass());
		proxy.save("jack");
	}

	/**
	 * JDk动态代理
	 */
	private static void testJdkProxy() {
		UserService userService = new UserServiceImpl();
		JDKProxyFactory jdkProxyFactory = new JDKProxyFactory();
		UserService proxy = (UserService) jdkProxyFactory.getProxy(userService);
		proxy.save("jack");
	}

	/**
	 * 静态代理
	 */
	private static void testStaticProxy() {
		UserServiceProxy proxy = new UserServiceProxy();
		proxy.save("jack");
	}
}
