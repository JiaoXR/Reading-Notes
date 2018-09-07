package com.jaxer.example.concurrent.executor;

import java.util.concurrent.TimeUnit;

/**
 * Created by jxr on 12:02 PM 2018/9/7
 */
public class LiftOff implements Runnable {

    protected int countDown = 10;
    private static int taskCount = 0;
    private final int id = taskCount++;

    public LiftOff() {
    }

    public LiftOff(int countDown) {
        this.countDown = countDown;
    }

    public String status() {
        return "#" + id + "(" + (countDown > 0 ? countDown : "Liftoff!") + "). ";
    }

    @Override
    public void run() {
        try {
            while (countDown-- > 0) {
                System.out.print(status());
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void run() {
//        while (countDown-- > 0) {
//            System.out.print(status());
//            Thread.yield();
//        }
//    }
}
