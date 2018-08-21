package com.jaxer.example.collection.advanced;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jxr on 4:02 PM
 */
public class FillingLists {
    public static void main(String[] args) {
        List<StringAddress> list = new ArrayList<>(
                Collections.nCopies(4, new StringAddress("hello"))
        );
        System.out.println(list);

        Collections.fill(list, new StringAddress("world!"));
        System.out.println(list);
    }
}
