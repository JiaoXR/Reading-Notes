package com.jaxer.example.property;

/**
 * Created by jxr on 5:18 PM
 */
public class PropertyTest {
    public static void main(String[] args) {
//        System.getProperties().list(System.out);
        System.out.println(System.getProperty("user.name"));
        System.out.println(System.getProperty("java.library.path"));
    }
}
