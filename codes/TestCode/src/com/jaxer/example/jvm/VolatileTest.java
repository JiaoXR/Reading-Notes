package com.jaxer.example.jvm;

import java.util.concurrent.TimeUnit;

/**
 * 测试 volatile 关键字
 * <p>
 * Created by jaxer on 2019-04-24
 */
public class VolatileTest {
	private static volatile int num = 0;

	private static void increment() {
		num++;
	}

	public static void main(String[] args) {
		new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + "\t come in");
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			increment();
			System.out.println(Thread.currentThread().getName() + "\t update num, value: " + num);
		}, "T1").start();

		while (num == 0) {

		}

		System.out.println(Thread.currentThread().getName() + " bye");
	}
}
