package com.jaxer.example.temp.neteasy;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by jaxer on 2019-02-23
 * 测试 CAS 机制（Unsafe 类相关操作）
 */
public class CasDemo {
	private static Unsafe unsafe;
	private static long valueOffset;

	static {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			unsafe = (Unsafe) theUnsafe.get(null);

			// 属性在对象内存中的偏移量，用于定位某个属性在内存中的位置
			valueOffset = unsafe.objectFieldOffset(CasDemo.class.getDeclaredField("value"));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private volatile int value = 0;

	private void increment() {
//		value++;

		// CAS 操作（失败后重试）
		int current;
		do {
			// 直接在内存中获取 value 属性（根据它在当前对象中的偏移量）
			current = unsafe.getIntVolatile(this, valueOffset);
		} while (!unsafe.compareAndSwapInt(this, valueOffset, current, current + 1));
	}

	public static void main(String[] args) throws InterruptedException {
		CasDemo demo = new CasDemo();

		for (int i = 0; i < 2; i++) {
			new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					demo.increment();
				}
			}).start();
		}
		Thread.sleep(1000);

		System.out.println("value = " + demo.value);
	}
}
