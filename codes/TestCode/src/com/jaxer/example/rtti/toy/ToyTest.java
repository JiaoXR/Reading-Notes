package com.jaxer.example.rtti.toy;

/**
 * Created by jxr on 3:42 PM
 * 运行时类型信息（RTTI）测试
 */
public class ToyTest {
    private static void printInfo(Class clz) {
        System.out.println("Class name: " + clz.getName() + ", is interface? [" + clz.isInterface() + "]");
        System.out.println("Simple name: " + clz.getSimpleName());
        System.out.println("Canonical name: " + clz.getCanonicalName());
        System.out.println("——————————————————————————");
    }

    public static void main(String[] args) {
        Class clz = null;
        try {
            clz = Class.forName("com.jaxer.example.rtti.toy.FancyToy");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        printInfo(clz);

        Class[] interfaces = clz.getInterfaces();
        for (Class anInterface : interfaces) {
            printInfo(anInterface);
        }

        Class up = clz.getSuperclass();
        Object object;
        try {
            object = up.newInstance();
            printInfo(object.getClass());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
