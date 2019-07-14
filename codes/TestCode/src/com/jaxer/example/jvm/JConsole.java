package com.jaxer.example.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaxer on 2019-04-03
 * VM Args: -Xms100m -Xmx100m -XX:+UseSerialGC
 * 测试 JConsole 工具
 */
public class JConsole {
	public static void main(String[] args) throws Exception {
//		fillHeap(1000);

//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		reader.readLine();
//		createBusyThread();
//		reader.readLine();
//		createLockThread(new Object());

		new Thread(new SyncAddRunnable(1, 2)).start();
		new Thread(new SyncAddRunnable(2, 1)).start();
	}

	static class SyncAddRunnable implements Runnable {
		int a, b;

		public SyncAddRunnable(int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public void run() {
			synchronized (Integer.valueOf(a)) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (Integer.valueOf(b)) {
					System.out.println(a + b);
				}
			}
		}
	}

	private static void createBusyThread() {
		new Thread(() -> {
			while (true)
				;
		}, "testBusyThread").start();
	}

	private static void createLockThread(final Object lock) {
		new Thread(() -> {
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "testLockThread").start();
	}

	/**
	 * 观察堆内存变化情况
	 */
	private static void fillHeap(int num) throws Exception {
		List<OOMObject> list = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			Thread.sleep(50);
			list.add(new OOMObject());
		}
		System.gc();
	}

	static class OOMObject {
		byte[] placeholder = new byte[64 * 1024];
	}
}
