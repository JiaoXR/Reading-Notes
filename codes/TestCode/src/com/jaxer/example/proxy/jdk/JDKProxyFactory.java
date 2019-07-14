package com.jaxer.example.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代码千万行，注释第一行。
 * JDK代理类生成工厂
 * <p>
 * Created by jaxer on 2019-07-13
 */
public class JDKProxyFactory {
	public Object getProxy(Object target) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				target.getClass().getInterfaces(), new MyInvocationHandler(target));
	}

	private static class MyInvocationHandler implements InvocationHandler {
		private Object target;

		public MyInvocationHandler(Object target) {
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("方法执行前-----");
			Object result = method.invoke(target, args);
			System.out.println("方法执行后-----");
			return result;
		}
	}
}
