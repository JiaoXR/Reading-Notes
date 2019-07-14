package com.jaxer.example.lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jaxer on 2019-05-23
 * 生产者&消费者模型
 */
public class ProdConsumerTest2 {
	private static final int SIZE = 10;
	private Random random = new Random();
	private Queue<Integer> queue = new LinkedList<>();

	private Lock lock = new ReentrantLock();
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();

	private void produce() throws InterruptedException {
		for (; ; ) {
			lock.lock();
			try {
				if (queue.size() >= SIZE) {
					notFull.await();
				}
				int nextInt = random.nextInt(1000);
				queue.offer(nextInt);

				sleep(400);
				System.out.println("size=" + queue.size() + ", 生产-->" + nextInt);
				notEmpty.signal();
			} finally {
				lock.unlock();
			}
		}
	}

	private void consume() throws InterruptedException {
		for (; ; ) {
			lock.lock();
			try {
				if (queue.size() <= 0) {
					notEmpty.await();
				}
				Integer poll = queue.poll();

				sleep(300);
				System.out.println("size=" + queue.size() + ", 消费成功-->" + poll);
				notFull.signal();
			} finally {
				lock.unlock();
			}
		}
	}

	private void sleep(int timeout) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ProdConsumerTest2 test = new ProdConsumerTest2();
		Thread t1 = new Thread(() -> {
			try {
				test.produce();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		Thread t2 = new Thread(() -> {
			try {
				test.consume();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		t1.start();
		t2.start();
	}
}
