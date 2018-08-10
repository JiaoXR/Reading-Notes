package com.jaxer.example.inner.closure;

/**
 * Created by jxr on 2:38 PM
 * 测试闭包
 */
public class Callbacks {
    public static void main(String[] args) {
        Callee1 callee1 = new Callee1();
        Callee2 callee2 = new Callee2();
        MyIncrement.f(callee2);

        Caller caller1 = new Caller(callee1);
        Caller caller2 = new Caller(callee2.getCallbackReference());
        caller1.go();
        caller1.go();
        caller2.go();
        caller2.go();
    }
}
