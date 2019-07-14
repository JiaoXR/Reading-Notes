package com.jaxer.example.lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by jaxer on 2019-05-23
 * 生产者&消费者模型
 */
public class ProdConsumerTest {
	private static final Object monitor = new Object();
	private Random random = new Random();
	private static final int SIZE = 10;
	private Queue<Integer> queue = new LinkedList<>();

	private void produce() throws InterruptedException {
		for (; ; ) {
			synchronized (monitor) {
				if (queue.size() >= SIZE) {
					monitor.wait();
				}
				int nextInt = random.nextInt(1000);
				queue.offer(nextInt);

				sleep(400);
				System.out.println("size=" + queue.size() + ", 生产-->" + nextInt);
				monitor.notify();
			}
		}
	}

	private void consume() throws InterruptedException {
		for (; ; ) {
			synchronized (monitor) {
				if (queue.size() <= 0) {
					monitor.wait();
				}
				Integer poll = queue.poll();

				sleep(300);
				System.out.println("size=" + queue.size() + ", 消费成功-->" + poll);
				monitor.notify();
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
		ProdConsumerTest test = new ProdConsumerTest();
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
