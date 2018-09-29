package com.jaxer.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jxr on 11:32 AM 2018/9/29
 */
public class Test {
    public static void main(String[] args) {
        testConvert();
        testRemoveAll();
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
