package com.jaxer.example.algorithm.search;

/**
 * Created by jiaoxiangru on 12:45 PM 2019/1/4
 * 简单二分查找及变体
 */
public class BinarySearch {
    public static void main(String[] args) {
        int[] data = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
//        System.out.println(bsearch1(data, 11));
//        System.out.println(bsearchRecursive(data, 11, 0, data.length - 1));

        // 查找第一个/最后一个值等于给定值的元素
//        int[] data2 = {1, 3, 5, 7, 9, 9, 9, 9, 17, 19};
//        System.out.println(bsearch(data2, 9));

        System.out.println(bsearch2(data, 10));
    }

    /**
     * 二分查找变体：查找 第一个值大于等于/最后一个值小于等于 给定值的元素
     */
    private static int bsearch2(int[] data, int value) {
        int low = 0;
        int high = data.length - 1;

        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            // 第一个值大于等于给定值的元素
//            if (data[mid] >= value) {
//                if (mid == 0 || data[mid - 1] < value) return mid;
//                else high = mid - 1;
//            } else {
//                low = mid + 1;
//            }

            // 最后一个值小于等于 给定值的元素
            if (data[mid] > value) {
                high = mid - 1;
            } else {
                if (mid == data.length - 1 || data[mid + 1] > value) return mid;
                else low = mid + 1;
            }
        }
        return -1;
    }

    /**
     * 二分查找变体：查找 第一个/最后一个 值等于给定值的元素（当数组中的元素重复时）
     */
    private static int bsearch1(int[] data, int value) {
        int low = 0;
        int high = data.length - 1;

        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (data[mid] < value) {
                low = mid + 1;
            } else if (data[mid] > value) {
                high = mid - 1;
            } else {
                // 查找第一个值等于给定值的元素
//                if (mid == 0 || data[mid - 1] != value) return mid;
//                else high = mid - 1;

                // 查找最后一个值等于给定值的元素
                if (mid == data.length - 1 || data[mid + 1] != value) return mid;
                else low = mid + 1;
            }
        }

        return -1;
    }

    /**
     * 简单二分查找（递归）
     */
    private static int bsearchRecursive(int[] data, int value, int low, int high) {
        if (low > high) return -1;

        int mid = low + ((high - low) >> 1);
        if (value == data[mid]) {
            return mid;
        } else if (value < data[mid]) {
            return bsearchRecursive(data, value, low, mid - 1);
        } else {
            return bsearchRecursive(data, value, mid + 1, high);
        }
    }

    /**
     * 简单的二分查找（循环）
     */
    private static int bsearch(int[] data, int value) {
        int low = 0;
        int high = data.length - 1;

        while (low <= high) {
//            int mid = (low + high) / 2; // 数组很大时可能溢出
//            int mid = low + (high - low) / 2;
            // 移位操作比除法更快（注意优先级，加括号）
            int mid = low + ((high - low) >> 1);
            if (value == data[mid]) {
                return mid;
            } else if (value < data[mid]) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return -1;
    }
}
