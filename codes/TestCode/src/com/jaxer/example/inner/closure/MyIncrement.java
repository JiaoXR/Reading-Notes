package com.jaxer.example.inner.closure;

/**
 * Created by jxr on 2:32 PM
 */
public class MyIncrement {
    public void increment() {
        System.out.println("Other operation");
    }

    static void f(MyIncrement myIncrement) {
        myIncrement.increment();
    }
}
