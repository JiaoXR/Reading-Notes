package com.jaxer.example.array;

import java.util.Arrays;

/**
 * Created by jxr on 2:56 PM
 * 基本类型多维数组
 */
public class MultidimensionalPrimitiveArray {
    public static void main(String[] args) {
        int[][] array = {{1, 2, 3}, {4, 5, 6}};
        System.out.println(Arrays.deepToString(array));

        int[][] arr = new int[3][2];
        System.out.println(Arrays.deepToString(arr));

        Integer[][] a = {{1, 2, 3}, {4, 5, 6}};
        System.out.println(Arrays.deepToString(a));
    }
}
