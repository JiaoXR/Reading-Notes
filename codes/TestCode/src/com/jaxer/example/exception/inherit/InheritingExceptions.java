package com.jaxer.example.exception.inherit;

/**
 * Created by jxr on 4:14 PM
 * 直接继承 Exception
 */
public class InheritingExceptions {
    private void f() throws SimpleException {
        System.out.println("throw SimpleException from f()");
        throw new SimpleException();
    }

    public static void main(String[] args) {
        InheritingExceptions sed = new InheritingExceptions();
        try {
            sed.f();
        } catch (SimpleException e) {
            System.out.println("Caught it!");
            e.printStackTrace();
        }
    }
}
