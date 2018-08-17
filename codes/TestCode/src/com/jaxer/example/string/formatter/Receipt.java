package com.jaxer.example.string.formatter;

import java.util.Formatter;

/**
 * Created by jxr on 3:34 PM
 * Formatter 控制空格与对齐
 */
public class Receipt {
    private double total = 0;
    private Formatter formatter = new Formatter(System.out);

    private void printTitle() {
        formatter.format("%-15s %5s %10s\n", "Item", "Qty", "Price");
        formatter.format("%-15s %5s %10s\n", "----", "---", "-----");
    }

    private void print(String name, int qty, double price) {
        formatter.format("%-15.15s %5d %10.2f\n", name, qty, price);
        total += price;
    }

    /**
     * s 表示字符串，d 表示整数，f 表示浮点数
     * %-15s: 15表示宽度，负号表示左对齐，不加负号为右对齐
     * %10.2f: .2f 表示保留两位小数
     */
    private void printTotal() {
        formatter.format("%-15s %5s %10.2f\n", "Tax", "", total * 0.06);
        formatter.format("%-15s %5s %10s\n", "", "", "-----");
        formatter.format("%-15s %5s %10.2f\n", "Item", "Qty", total * 1.06);
    }

    /**
     * 打印结果：
     *
     * Item              Qty      Price
     * ----              ---      -----
     * Jack's Magic Be     4       4.25
     * Princess Peas       3       5.10
     * Three Bears Por     4      14.29
     * Tax                         1.42
     *                            -----
     * Item              Qty      25.06
     */
    public static void main(String[] args) {
        Receipt receipt = new Receipt();
        receipt.printTitle();
        receipt.print("Jack's Magic Be", 4, 4.25);
        receipt.print("Princess Peas", 3, 5.1);
        receipt.print("Three Bears Porridge", 4, 14.29);
        receipt.printTotal();
    }
}
