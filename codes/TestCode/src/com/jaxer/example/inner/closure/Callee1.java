package com.jaxer.example.inner.closure;

/**
 * Created by jxr on 2:31 PM
 */
public class Callee1 implements Incrementable {
    private int i = 0;

    @Override
    public void increment() {
        i++;
        System.out.println(i);
    }
}
