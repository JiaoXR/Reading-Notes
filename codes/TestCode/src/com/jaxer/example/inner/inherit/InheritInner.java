package com.jaxer.example.inner.inherit;

/**
 * Created by jxr on 3:34 PM
 * 内部类的继承
 */
public class InheritInner extends WithInner.Inner {
//    Won't compile
//    InheritInner() {
//    }

    // 注意构造器要这样写，才能编译通过
    InheritInner(WithInner withInner) {
        withInner.super();
    }

    public static void main(String[] args) {
        WithInner withInner = new WithInner();
        InheritInner inheritInner = new InheritInner(withInner);
    }
}
