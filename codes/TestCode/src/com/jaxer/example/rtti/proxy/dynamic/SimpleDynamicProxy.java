package com.jaxer.example.rtti.proxy.dynamic;

import com.jaxer.example.rtti.proxy.Interface;
import com.jaxer.example.rtti.proxy.RealObject;

import java.lang.reflect.Proxy;

/**
 * Created by jxr on 7:24 PM
 */
public class SimpleDynamicProxy {
    private static void consumer(Interface iface) {
        iface.doSomething();
        iface.somethingElse("hello");
    }

    public static void main(String[] args) {
        RealObject realObject = new RealObject();
        consumer(realObject);
        System.out.println("-------");

        Interface proxyInstance = (Interface) Proxy.newProxyInstance(Interface.class.getClassLoader(),
                new Class[]{Interface.class},
                new DynamicProxyHandler(realObject));
        consumer(proxyInstance);
    }
}
