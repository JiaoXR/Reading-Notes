package com.jaxer.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jxr on 11:32 AM 2018/9/29
 */
public class Test {
    public static void main(String[] args) {
//        testConvert();
//        testRemoveAll();
        sort();
    }

    private static void sort() {
        List<Integer> list = Arrays.asList(1, 7, 5, 9, 2, 6, 4);
        // 从大到小
        list.sort((o1, o2) -> (o2 - o1));
        System.out.println(list);

        // 测试 return
        list.forEach(e -> {
            if (e > 5) return;
            System.out.println("e->" + e);
        });
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
