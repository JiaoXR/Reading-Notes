package com.jaxer.example.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jaxer on 2019-05-28
 * 模拟高并发转账模型
 */
public class MyLock {
	private static final int COUNT = 9999;

	public static void main(String[] args) throws InterruptedException {
		Account source = new Account(10000);
		Account target = new Account(10000);
		CountDownLatch countDownLatch = new CountDownLatch(COUNT);
		for (int i = 0; i < COUNT; i++) {
			new Thread(() -> {
				source.transfer(1, target);
				countDownLatch.countDown();
			}).start();
		}
		countDownLatch.await();

		System.out.println("source = " + source.getBalance());
		System.out.println("target = " + target.getBalance());
	}

	static class Allocator {
		private Allocator() {
		}

		private List<Account> locks = new ArrayList<>();

		public synchronized void apply(Account source, Account target) {
			while (locks.contains(source) || locks.contains(target)) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			locks.add(source);
			locks.add(target);
		}

		public synchronized void release(Account source, Account target) {
			locks.remove(source);
			locks.remove(target);
			this.notifyAll();
		}

		public static Allocator getInstance() {
			return AllocatorSingleton.instance;
		}

		static class AllocatorSingleton {
			static Allocator instance = new Allocator();
		}
	}

	static class Account {
		private Integer balance;

		public Account(Integer balance) {
			this.balance = balance;
		}

		public void transfer(Integer money, Account target) {
			Allocator.getInstance().apply(this, target);
			this.balance -= money;
			target.setBalance(target.getBalance() + money);
			Allocator.getInstance().release(this, target);
		}

		public Integer getBalance() {
			return balance;
		}

		public void setBalance(Integer balance) {
			this.balance = balance;
		}
	}
}
