package com.jaxer.example.concurrent.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jxr on 3:07 PM 2018/9/7
 */
public class CallableTest {
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(new TaskWithResult(i)));
        }
//        futures.forEach(System.out::println);
        for (Future<String> future : futures) {
            System.out.println(future.get());
        }

        executorService.shutdown();
    }
}
