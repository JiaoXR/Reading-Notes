package com.jaxer.example.exception.runtime;

/**
 * Created by jxr on 6:54 PM
 * RuntimeException 无需显式在方法中声明
 */
public class NeverCaught {
    private static void f() {
        throw new RuntimeException("From f()");
    }

    private static void g() {
        f();
    }

    public static void main(String[] args) {
        g();
    }
}
