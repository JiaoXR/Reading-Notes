package com.jaxer.example.temp;

import java.util.LinkedList;
import java.util.List;

/**
 * 源码阅读测试
 * <p>
 * Created by jaxer on 2019-02-19
 */
public class TestSource {
	public static void main(String[] args) {

		testLinkedList();
	}

	private static void testLinkedList() {
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i < 1000; i++) {
			list.add(i);
		}
		System.out.println(list);

//		Thread thread1 = new Thread(() -> {
//			for (Integer integer : list) {
//				System.out.println(integer);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		Thread thread2 = new Thread(() -> {
//			for (Integer integer : list) {
//				System.out.println(integer);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		thread1.start();
//		thread2.start();
	}
}
