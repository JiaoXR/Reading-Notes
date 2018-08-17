package com.jaxer.example.exception.custom;

/**
 * Created by jxr on 5:10 PM
 * 自定义含参异常
 */
public class MyException extends Exception {
    public MyException() {
    }

    public MyException(String msg) {
        super(msg);
    }
}
