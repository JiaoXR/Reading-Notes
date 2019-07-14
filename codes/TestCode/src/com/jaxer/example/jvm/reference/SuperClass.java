package com.jaxer.example.jvm.reference;

/**
 * 主动引用与被动引用
 * <p>
 * Created by jaxer on 2019-04-23
 */
public class SuperClass {
	static {
		System.out.println("SuperClass init!");
	}

	public static int value = 123;

	public static final String HELLO_WORLD = "hello, world";
}
