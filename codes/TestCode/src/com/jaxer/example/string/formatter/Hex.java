package com.jaxer.example.string.formatter;

import java.io.FileInputStream;

/**
 * Created by jxr on 4:39 PM
 * 一个十六进制转储（dump）工具
 */
public class Hex {
    private static final String FILE_NAME = "~/Desktop/temp.js";

    private static String format(byte[] data) {
        StringBuilder result = new StringBuilder();
        int n = 0;
        for (byte b : data) {
            if (n % 16 == 0) {
                result.append(String.format("%05X: ", n));
            }
            result.append(String.format("%02X ", b));
            n++;
            if (n % 16 == 0) {
                result.append("\n");
            }
        }
        result.append("\n");
        return result.toString();
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        FileInputStream fileInputStream;
        int b;
        try {
            fileInputStream = new FileInputStream(FILE_NAME);
            while ((b = fileInputStream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(format(stringBuilder.toString().getBytes()));
    }
}
