package com.jaxer.example.collection.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by jxr on 5:52 PM
 * 测试 Random 的随机性
 */
public class MapTest {
    private static final Integer MAP_SIZE = 10000;

    public static void main(String[] args) {
        Random random = new Random(47);
        Map<Integer, Integer> map = new HashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            int r = random.nextInt(20);
            Integer freq = map.get(r);
            map.put(r, freq == null ? 1 : freq + 1);
        }
        System.out.println(map);
    }
}
