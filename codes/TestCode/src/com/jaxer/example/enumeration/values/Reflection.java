package com.jaxer.example.enumeration.values;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by jxr on 7:04 PM 2018/8/27
 * valuesOf 方法怎么来的？
 */
public class Reflection {
    private static void analyze(Class<?> enumClass) {
        System.out.println("---------  Analysing " + enumClass + "-------");
        System.out.print("Interfaces: ");
        Type[] genericInterfaces = enumClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            System.out.print(genericInterface + ", ");
        }
        System.out.println();
        System.out.println("Base: " + enumClass.getSuperclass());

        Set<String> methods = new TreeSet<>();
        for (Method method : enumClass.getMethods()) {
            methods.add(method.getName());
        }
        System.out.println("Methods: " + methods);
    }

    public static void main(String[] args) {
        analyze(Explore.class);
        analyze(Enum.class);
    }
}
