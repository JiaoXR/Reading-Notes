package com.jaxer.example.concurrent.callable;

import java.util.concurrent.Callable;

/**
 * Created by jxr on 3:06 PM 2018/9/7
 */
public class TaskWithResult implements Callable<String> {
    private int id;

    public TaskWithResult(int id) {
        this.id = id;
    }

    @Override
    public String call() {
        return "result of TaskWithResult: " + id;
    }
}
