package com.jaxer.example.jvm;

import java.util.Map;

/**
 * 查看GC日志
 * <p>
 * Created by jaxer on 2019-03-27
 * VM Args: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
 */
public class GC {
	private static final int _1MB = 1024 * 1024;

	public static void main(String[] args) {
		testAllocation();
//		testPretenureSizeThreshold();

		for (Map.Entry<Thread, StackTraceElement[]> threadEntry : Thread.getAllStackTraces().entrySet()) {
			Thread thread = threadEntry.getKey();
			StackTraceElement[] stackTraceElements = threadEntry.getValue();
			if (thread.equals(Thread.currentThread())) {
				continue;
			}
			System.out.println("\n线程：" + thread.getName() + "\n");
			for (StackTraceElement traceElement : stackTraceElements) {
				System.out.println("\t" + traceElement + "\t");
			}
		}

		while (true) ;
	}

	private static void testPretenureSizeThreshold() {
		byte[] allocation;
		allocation = new byte[4 * _1MB];
	}

	private static void testAllocation() {
		byte[] allocation1, allocation2, allocation3, allocation4;
		allocation1 = new byte[2 * _1MB];
		allocation2 = new byte[2 * _1MB];
		allocation3 = new byte[2 * _1MB];
		allocation4 = new byte[2 * _1MB];
	}
}
