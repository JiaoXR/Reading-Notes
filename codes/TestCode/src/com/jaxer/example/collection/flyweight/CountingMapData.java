package com.jaxer.example.collection.flyweight;

import java.util.AbstractMap;
import java.util.Set;

/**
 * Created by jxr on 2:45 PM 2018/8/27
 */
public class CountMapData extends AbstractMap<Integer, String> {
    private int size;
    private static String[] chars = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");

    

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        return null;
    }
}
