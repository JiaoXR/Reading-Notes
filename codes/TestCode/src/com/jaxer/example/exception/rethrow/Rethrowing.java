package com.jaxer.example.exception.rethrow;

/**
 * Created by jxr on 6:11 PM
 * 异常重新抛出
 * fillInStackTrace
 */
public class Rethrowing {
    private static void f() throws Exception {
        System.out.println("originating the exception in f()");
        throw new Exception();
    }

    private static void g() throws Exception {
        try {
            f();
        } catch (Exception e) {
            System.out.println("Inside g(), e.printStackTrace()");
            e.printStackTrace(System.out);
            throw e;
        }
    }

    private static void h() throws Exception {
        try {
            f();
        } catch (Exception e) {
            System.out.println("Inside h(), e.printStackTrace()");
            e.printStackTrace(System.out);
            // 调用 fillInStackTrace 的地方会成为异常的新发生地
            throw (Exception) e.fillInStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            g();
        } catch (Exception e) {
            System.out.println("main: printStackTrace()");
            e.printStackTrace(System.out);
        }

        try {
            h();
        } catch (Exception e) {
            System.out.println("main: printStackTrace()");
            e.printStackTrace(System.out);
        }
    }
}
