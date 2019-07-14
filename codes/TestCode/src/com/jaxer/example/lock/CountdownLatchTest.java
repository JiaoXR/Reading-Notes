package com.jaxer.example.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jaxer on 2019-05-29
 */
public class CountdownLatchTest {
	private static final int count = 10000;

	public static void main(String[] args) throws InterruptedException {
//		CountDownLatch countDownLatch = new CountDownLatch(count);
//		int number = 0;
//		Number number1 = new Number(number);
//
//		for (int i = 0; i < count; i++) {
//			new Thread(number1).start();
////			System.out.println("-->" + number1.number);
//			countDownLatch.countDown();
//		}
//		countDownLatch.await();
//
//		System.out.println("number = " + number1.number);

		test2();

//		testCDL();
	}

	public static void testCDL() throws InterruptedException {
		int count = 5;
		CountDownLatch countDownLatch = new CountDownLatch(count);
		for (int i = 1; i <= count; i++) {
			int finalI = i;
			new Thread(() -> {
				try {
					TimeUnit.SECONDS.sleep(finalI);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " is working ..");
//				countDownLatch.countDown();
			}).start();
		}
//		countDownLatch.await();
		System.out.println(Thread.currentThread().getName() + " go on ..");
	}

	private static volatile List<Integer> list = new ArrayList<>();

	private static void test2() {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		new Thread(() -> {
			if (list.size() != 5) {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(Thread.currentThread().getName() + " start..");
		}).start();

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				list.add(i);
				System.out.println(Thread.currentThread().getName() + " add " + i);
				if (list.size() == 5) {
					countDownLatch.countDown();
				}
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	static class Number implements Runnable {
		private int number;

		public Number(int number) {
			this.number = number;
		}

		private void increment() {
			number++;
		}

		@Override
		public void run() {
			number++;
//			increment();
//			try {
//				TimeUnit.MILLISECONDS.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
}
