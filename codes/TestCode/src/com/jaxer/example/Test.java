package com.jaxer.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jxr on 11:32 AM 2018/9/29
 */
public class Test {
	public static void main(String[] args) throws InterruptedException {
//        testConvert();
//        testRemoveAll();
//        sort();
//		System.out.println(1 << 1);
//		testHashMap();

//		int a = 0, b = 0;
//		System.out.println(a);

//		testLinkedHashMap();

//		lruCache.get("bush");
//		System.out.println(lruCache);
//		testHashtable();

		testHashMap2();
	}

	/**
	 * 测试HashMap的并发性
	 */
	private static void testHashMap2() throws InterruptedException {
		Map<Integer, Integer> map = new ConcurrentHashMap<>();
		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 5000; i++) {
				map.put(i, i);
			}
		});

		Thread t2 = new Thread(() -> {
			for (int i = 5000; i < 10000; i++) {
				map.put(i, i);
			}
		});

		t1.start();
		t2.start();

		TimeUnit.SECONDS.sleep(20);
		System.out.println(map);
		System.out.println("size: " + map.size());

//		Thread t3 = new Thread(() -> {
//			for (int i = 0; i < 5000; i++) {
//				map.remove(i);
//			}
//		});
//		Thread t4 = new Thread(() -> {
//			for (int i = 5000; i < 10000; i++) {
//				map.remove(i);
//			}
//		});
//		t3.start();
//		t4.start();
//
//		TimeUnit.SECONDS.sleep(20);
//		System.out.println(map);
//		System.out.println("size: " + map.size());
	}

	private static void testHashtable() {
		Map<String, String> hashtable = new Hashtable<>();
		hashtable.put("a", "a");
		hashtable.put("b", "b");
		for (Map.Entry<String, String> entry : hashtable.entrySet()) {
			System.out.println(entry);
		}
	}

	private static void testLRU() {
		LRUCache<String, String> lruCache = new LRUCache<>(2);
		lruCache.put("bush", "a");
		lruCache.put("obama", "b");
		lruCache.put("trump", "c");
		System.out.println(lruCache);
	}

	private static class LRUCache<K, V> extends LinkedHashMap<K, V> {
		private int capacity;

		public LRUCache(int capacity) {
			super(16, 0.75f, true);
			this.capacity = capacity;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > capacity;
		}
	}

	private static void testLinkedHashMap() {
		Map<String, String> map = new LinkedHashMap<>(2, 0.75f, true);
		map.put("bush", "a");
		map.put("obama", "b");
		map.put("trump", "c");
		map.put("lincoln", "d");
		System.out.println(map);
		map.get("obama");
		System.out.println(map);
	}

	private static void testHashMap() {
		Map<String, String> map = new HashMap<>(2);
		map.put("bush", "a");
		map.put("obama", "b");
		map.put("trump", "c");
		map.put("lincoln", "d");
		System.out.println(map);
	}

	private static void sort() {
		List<Integer> list = Arrays.asList(1, 7, 5, 9, 2, 6, 4);
		// 从大到小
//        list.sort((o1, o2) -> (o2 - o1));
		System.out.println(list);

		// 测试 return
//        list.forEach(e -> {
//            if (e > 5) return;
//            System.out.println("e->" + e);
//        });
	}

	private static void testConvert() {
		String num = "100.0";
		Integer.parseInt(num); //NumberFormatException
	}

	private static void testRemoveAll() {
		List<String> list = Arrays.asList("1", "2", "3", "4", "5");
		List<String> list0 = Arrays.asList("1", "2");
		ArrayList<String> strings = new ArrayList<>(list);
		strings.removeAll(list0);
	}
}
