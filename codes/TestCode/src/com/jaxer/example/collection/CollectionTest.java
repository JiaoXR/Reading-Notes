package com.jaxer.example.collection;

import java.util.*;

/**
 * Created by jxr on 11:05 AM
 * 测试集合类的打印
 */
public class CollectionTest {
    private static Collection<String> fill(Collection<String> collection) {
        collection.add("rat");
        collection.add("cat");
        collection.add("dog");
        collection.add("dog");
        return collection;
    }

    private static Map<String, String> fill(Map<String, String> map) {
        map.put("rat", "Fuzzy");
        map.put("cat", "Rags");
        map.put("dog", "Bosco");
        map.put("dog", "Spot");
        return map;
    }

    private static void print(Object object) {
        System.out.println(object);
    }

    /* 输出结果：
     * [rat, cat, dog, dog]
     * [rat, cat, dog, dog]
     * [rat, cat, dog]
     * [cat, dog, rat]
     * [rat, cat, dog]
     * {rat=Fuzzy, cat=Rags, dog=Spot}
     * {cat=Rags, dog=Spot, rat=Fuzzy}
     * {rat=Fuzzy, cat=Rags, dog=Spot}
     */
    public static void main(String[] args) {
        print(fill(new ArrayList<>()));
        print(fill(new LinkedList<>()));
        print(fill(new HashSet<>()));
        print(fill(new TreeSet<>()));
        print(fill(new LinkedHashSet<>()));
        print(fill(new HashMap<>()));
        print(fill(new TreeMap<>()));
        print(fill(new LinkedHashMap<>()));
    }
}
