package com.jaxer.example.exception.stacktrace;

/**
 * Created by jxr on 5:57 PM
 * 栈轨迹
 */
public class WhoCalled {
    private static void f() {
        try {
            throw new Exception();
        } catch (Exception e) {
            for (StackTraceElement element : e.getStackTrace()) {
                System.out.println(element.getMethodName());
            }
        }
    }

    private static void g() {
        f();
    }

    private static void h() {
        g();
    }

    /*
        输出结果：
        f
        g
        h
        main
     */
    public static void main(String[] args) {
        h();
    }
}
