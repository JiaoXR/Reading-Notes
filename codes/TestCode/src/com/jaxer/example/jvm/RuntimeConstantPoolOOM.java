package com.jaxer.example.jvm;

/**
 * 运行时常量池
 * <p>
 * Created by jaxer on 2019-03-20
 */
public class RuntimeConstantPoolOOM {
	public static void main(String[] args) {
		String s1 = new StringBuilder("计算机").append("软件").toString();
		System.out.println(s1.intern() == s1); //true

		String s2 = new StringBuilder("ja").append("va").toString();
		System.out.println(s2.intern() == s2); //false
	}
}
