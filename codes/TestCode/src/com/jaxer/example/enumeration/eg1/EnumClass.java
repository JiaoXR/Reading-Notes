package com.jaxer.example.enumeration;

import static com.jaxer.example.enumeration.Shrubbery.*;

/**
 * Created by jxr on 6:30 PM 2018/8/27
 * 测试枚举类型
 */
public class EnumClass {
    public static void main(String[] args) {
        for (Shrubbery shrubbery : Shrubbery.values()) {
            System.out.println(shrubbery + " ordinal: " + shrubbery.ordinal());
            System.out.print(shrubbery.compareTo(CRAWLING) + " ");
            System.out.print(shrubbery.equals(CRAWLING) + " ");
            System.out.println(shrubbery == CRAWLING);
            System.out.println("class: " + shrubbery.getDeclaringClass());
            System.out.println("name: " + shrubbery.name());
            System.out.println("-------");
        }
    }
}
