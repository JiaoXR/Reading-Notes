package com.jaxer.example.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jxr on 5:54 PM 2018/8/31
 * 自定义注解处理器
 */
public class UseCaseTracker {
    private static void trackUseCase(List<Integer> useCases, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            UseCase useCase = method.getAnnotation(UseCase.class);
            if (useCase != null) {
                System.out.println("Found useCase: " + useCase.id() + ", description: " + useCase.description());
            }
        }
        System.out.println("useCases: " + useCases);
    }

    public static void main(String[] args) {
        List<Integer> useCases = new ArrayList<>();
        Collections.addAll(useCases, 47, 48, 49);
        trackUseCase(useCases, PasswordUtils.class);
    }
}
