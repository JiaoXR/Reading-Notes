package com.jaxer.example.concurrent.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jxr on 12:06 PM 2018/9/7
 */
public class FixedThreadPool {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 5; i++) {
            executorService.execute(new LiftOff());
        }
        executorService.shutdown();
    }
}
