package com.jaxer.example.enumeration;

/**
 * Created by jxr on 4:48 PM 2018/8/31
 * 使用接口组织枚举
 */
public interface Food {
    enum Appetizer implements Food {
        SALAD, SOUP, SPRING_ROLLS;
    }

    enum MainCourse implements Food {
        LASAGNE, BURRITO, VINDALOO;
    }

    enum Coffee implements Food {
        BLACL_COFFEE, TEA;
    }

    public static void main(String[] args) {
        Food food = Appetizer.SALAD;
        food = MainCourse.BURRITO;
        food = Coffee.BLACL_COFFEE;
        System.out.println(food);
    }
}
