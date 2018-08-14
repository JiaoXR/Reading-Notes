package com.jaxer.example.property;

import java.util.Map;
import java.util.Set;

/**
 * Created by jxr on 5:18 PM
 */
public class PropertyTest {
    public static void main(String[] args) {
//        System.getProperties().list(System.out);
        System.out.println(System.getProperty("user.name"));
        System.out.println(System.getProperty("java.library.path"));

        getSystemEnvironment();
    }

    /**
     * 获取所有的操作系统环境变量
     */
    private static void getSystemEnvironment() {
        Set<Map.Entry<String, String>> entrySet = System.getenv().entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            System.out.println(entry);
        }
    }
}
