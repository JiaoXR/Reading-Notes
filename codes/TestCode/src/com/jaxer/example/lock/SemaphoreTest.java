package com.jaxer.example.lock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 代码千万行，注释第一行。
 * Semaphore测试
 * <p>
 * Created by jaxer on 2019-07-09
 */
public class SemaphoreTest {
	public static void main(String[] args) {
		Semaphore semaphore = new Semaphore(1);

		for (int i = 0; i < 50; i++) {
			new Thread(() -> {
				try {
					semaphore.acquire();
					System.out.println(Thread.currentThread().getName() + " 正在执行..");
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}).start();
		}
	}
}
