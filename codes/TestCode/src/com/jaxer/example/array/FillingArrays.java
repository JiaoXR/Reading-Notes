package com.jaxer.example.array;

import java.util.Arrays;

/**
 * Created by jxr on 3:16 PM
 * Arrays.fill() 填充数组
 */
public class FillingArrays {
    public static void main(String[] args) {
        int size = 6;
        boolean[] booleans = new boolean[size];
        byte[] bytes = new byte[size];
        char[] chars = new char[size];
        short[] shorts = new short[size];
        int[] ints = new int[size];
        long[] longs = new long[size];
        float[] floats = new float[size];
        double[] doubles = new double[size];
        String[] strings = new String[size];

        Arrays.fill(booleans, true);
        System.out.println("booleans: " + Arrays.toString(booleans));

        Arrays.fill(bytes, (byte) 11);
        System.out.println("bytes: " + Arrays.toString(bytes));

        Arrays.fill(chars, 'x');
        System.out.println("chars: " + Arrays.toString(chars));

        Arrays.fill(shorts, (short) 17);
        System.out.println("shorts: " + Arrays.toString(shorts));

        Arrays.fill(ints, 15);
        System.out.println("bytes: " + Arrays.toString(ints));

        Arrays.fill(longs, 23);
        System.out.println("longs: " + Arrays.toString(longs));

        Arrays.fill(floats, 27);
        System.out.println("floats: " + Arrays.toString(floats));

        Arrays.fill(doubles, 29);
        System.out.println("doubles: " + Arrays.toString(doubles));

        Arrays.fill(strings, "World");
        System.out.println("strings: " + Arrays.toString(strings));
    }
}
