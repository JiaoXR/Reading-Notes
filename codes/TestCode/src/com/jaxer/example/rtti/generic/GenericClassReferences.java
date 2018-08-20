package com.jaxer.example.rtti.generic;

/**
 * Created by jxr on 4:23 PM
 */
public class GenericClassReferences {
    public static void main(String[] args) {
        Class intClass = int.class;
        Class<Integer> genericIntClass = int.class;
        genericIntClass = Integer.class;
        intClass = double.class;
//        genericIntClass = double.class; //illegal, 使用 Class<? extends Number> 没问题
    }
}
