package com.jaxer.example.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jxr on 5:25 PM 2018/11/14
 * 测试交集等操作
 */
public class IntersectionTest {
    public static void main(String[] args) {
        List<Integer> list1 = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            list1.add(i);
        }
        List<Integer> list2 = Arrays.asList(3, 4, 5, 6, 7);
        list1.removeAll(list2);
        System.out.println(list1);
    }
}
