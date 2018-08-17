package com.jaxer.example.exception.finall;

/**
 * Created by jxr on 10:19 AM
 * finally 块中的代码会被执行（即使有 return 语句）
 */
public class MultipleReturns {
    private static void f(int i) {
        System.out.println("Initialize that requires cleanup");
        try {
            System.out.println("point 1");
            if (i == 1) return;
            System.out.println("point 2");
            if (i == 2) return;
            System.out.println("point 3");
            if (i == 3) return;
            System.out.println("end");
            return;
        } finally {
            System.out.println("Performing cleanup");
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 4; i++) {
            f(i);
        }
    }
}
