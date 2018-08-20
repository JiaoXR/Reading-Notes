package com.jaxer.example.rtti.toy;

/**
 * Created by jxr on 4:34 PM
 */
public class GenericToyTest {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Class<FancyToy> fancyToyClass = FancyToy.class;
        // 使用泛型后，newInstance 方法将返回该对象的确切类型，而不仅仅只是 Object 类型
        FancyToy fancyToy = fancyToyClass.newInstance();

        Class<? super FancyToy> superclass = fancyToyClass.getSuperclass();
        Object object = superclass.newInstance();
    }
}
