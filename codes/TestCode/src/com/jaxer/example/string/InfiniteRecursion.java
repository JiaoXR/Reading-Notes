package com.jaxer.example.string;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxr on 11:34 AM
 * 无意识的递归
 */
public class InfiniteRecursion {
    @Override
    public String toString() {
        /*
         * 这里发生了自动类型转换：由 InfiniteRecursion 类型转为 String 类型。
         * 由于编译器看到一个 String 对象后面跟着一个 "+"，之后的对象不再是 String，
         * 于是编译器试着将 this 转为一个 String，会调用 this 的 toString 方法，这样会产生 toString() 的递归，
         * 导致 StackOverflowError
         */
//        return " InfiniteRecursion address: " + this + "\n";
        return super.toString();
    }

    public static void main(String[] args) {
        List<InfiniteRecursion> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new InfiniteRecursion());
        }
        System.out.println(list);
    }
}
