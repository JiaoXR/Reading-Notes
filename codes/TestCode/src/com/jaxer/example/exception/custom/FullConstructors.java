package com.jaxer.example.exception.custom;

/**
 * Created by jxr on 5:11 PM
 * 自定义含参异常
 */
public class FullConstructors {
    private static void f() throws MyException {
        System.out.println("Throwing MyException from f()");
        throw new MyException();
    }

    private static void g() throws MyException {
        System.out.println("Throwing MyException from g()");
        throw new MyException("Originated in g()");
    }

    public static void main(String[] args) {
        try {
            f();
        } catch (MyException e) {
            e.printStackTrace();
        }

        try {
            g();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
