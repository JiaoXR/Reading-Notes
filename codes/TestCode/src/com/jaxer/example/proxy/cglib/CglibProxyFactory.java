package com.jaxer.example.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代码千万行，注释第一行。
 * CGLib代理工厂
 * <p>
 * Created by jaxer on 2019-07-13
 */
public class CglibProxyFactory {
	public Object getProxy(Class<?> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new MyMethodInterceptor());
		return enhancer.create();
	}

	private static class MyMethodInterceptor implements MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			System.out.println("cglib方法调用前");
			Object o = proxy.invokeSuper(obj, args);
			System.out.println("cglib方法调用后");
			return o;
		}
	}
}
