package com.jaxer.example.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jxr on 6:03 PM 2018/11/15
 * 测试方法 scheduleAtFixedRate 与 scheduleWithFixedDelay 的区别
 */
public class ScheduledExecutorServiceTest {
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        test1();
//        test2();
    }

    /**
     * 以固定的频率执行
     */
    private static void test1() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("currentSeconds-->" + System.currentTimeMillis() / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * 以固定的时间间隔执行
     */
    private static void test2() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("currentSeconds-->" + System.currentTimeMillis() / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
