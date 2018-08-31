package com.jaxer.example.enumeration.constant;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jxr on 5:10 PM 2018/8/31
 * 枚举常量相关的方法
 */
public enum ConstantSpecificMethod {
    DATE_TIME {
        String getInfo() {
            return DateFormat.getDateInstance().format(new Date());
        }
    },
    CLASS_PATH {
        String getInfo() {
            return System.getenv("CLASSPATH");
        }
    };

    abstract String getInfo();

    public static void main(String[] args) {
        for (ConstantSpecificMethod constantSpecificMethod : values()) {
            System.out.println(constantSpecificMethod.getInfo());
        }
    }
}
