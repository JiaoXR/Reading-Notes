package com.jaxer.example.annotation;

/**
 * Created by jxr on 5:50 PM 2018/8/31
 * 自定义注解的使用场景
 */
public class PasswordUtils {
    @UseCase(id = 47, description = "password must contain at least one numeric")
    public boolean validatePassword(String password) {
        return password.matches("\\w*\\d\\w*");
    }

    @UseCase(id = 48)
    public String encryptPassword(String password) {
        return new StringBuilder(password).reverse().toString();
    }
}
