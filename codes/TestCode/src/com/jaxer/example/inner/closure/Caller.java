package com.jaxer.example.inner.closure;

/**
 * Created by jxr on 2:37 PM
 */
public class Caller {
    private Incrementable callbackReference;

    Caller(Incrementable callbackReference) {
        this.callbackReference = callbackReference;
    }

    void go() {
        callbackReference.increment();
    }
}
