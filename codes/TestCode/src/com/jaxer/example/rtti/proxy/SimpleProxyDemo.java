package com.jaxer.example.rtti.proxy;

/**
 * Created by jxr on 6:59 PM
 * 静态代理
 */
public class SimpleProxyDemo {
    private static void consumer(Interface iface) {
        iface.doSomething();
        iface.somethingElse("hello");
    }

    public static void main(String[] args) {
        consumer(new RealObject());
        System.out.println("----------");
        consumer(new SimpleProxy(new RealObject()));
    }
}
