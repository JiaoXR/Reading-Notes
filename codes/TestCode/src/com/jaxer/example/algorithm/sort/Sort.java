package com.jaxer.example.algorithm.sort;

import java.util.Arrays;

/**
 * Created by jiaoxiangru on 8:17 PM 2019/1/2
 */
public class Sort {
    public static void main(String[] args) {
        int[] arr = {11, 8, 3, 9, 7, 1, 2, 5};
//        bubbleSort(arr);
//        insertion(arr);
        merge(arr);
    }

    /**
     * 归并排序
     */
    private static void merge(int[] data) {
        mergeSort(data, 0, data.length - 1);
        System.out.println(Arrays.toString(data));
    }

    private static void mergeSort(int[] data, int low, int high) {
        if (low >= high) return;
        int mid = (low + high) / 2;
        mergeSort(data, low, mid);
        mergeSort(data, mid + 1, high);
        mergeData(data, low, mid, high);
    }

    // 合并数据
    private static void mergeData(int[] data, int low, int mid, int high) {
        int[] tempArr = new int[high - low + 1];
        int i = low;
        int j = mid + 1;
        int k = 0;

        while (i <= mid && j <= high) {
            if (data[i] < data[j]) {
                tempArr[k++] = data[i++];
            } else {
                tempArr[k++] = data[j++];
            }
        }
        while (i <= mid) {
            tempArr[k++] = data[i++];
        }
        while (j <= high) {
            tempArr[k++] = data[j++];
        }
//        while (i <= high) {
//            data[i] = tempArr[i++];
//        }
//        System.arraycopy(tempArr, 0, data, low, tempArr.length);
        for (int k2 = 0; k2 < tempArr.length; k2++) {
            data[k2 + low] = tempArr[k2];
        }
        System.out.println(Arrays.toString(tempArr));
    }

    /**
     * 插入排序
     */
    private static void insertion(int[] a) {
        System.out.println("排序前：" + Arrays.toString(a));
        int length = a.length;
        for (int i = 1; i < length; ++i) {
            int value = a[i];
            int j = i - 1;
            for (; j >= 0; --j) {
                if (a[j] > value) {
                    a[j + 1] = a[j];
                } else {
                    break;
                }
            }
            a[j + 1] = value;
        }
        System.out.println("排序后：" + Arrays.toString(a));
    }

    /**
     * 冒泡排序
     */
    private static void bubbleSort(int[] a) {
        System.out.println("排序前：" + Arrays.toString(a));
        int length = a.length;
        boolean flag = false;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length - 1; j++) {
                if (a[j] > a[j + 1]) {
                    int temp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = temp;
                    flag = true;
                }
            }
            if (!flag) break;
        }
        System.out.println("排序后：" + Arrays.toString(a));
    }
}
