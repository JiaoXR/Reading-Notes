package com.jaxer.example;

import java.util.concurrent.TimeUnit;

/**
 * 测试ThreadLocal
 * <p>
 * Created by jaxer on 2019-06-27
 * 内存泄漏问题？
 */
public class ThreadLocalTest {
	static ThreadLocal<Person> threadLocal = new ThreadLocal<>();

	public static void main(String[] args) {
		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(threadLocal.get());
		}).start();

		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threadLocal.set(new Person());
		}).start();
	}

	static class Person {
		String name = "Jack";
	}
}
