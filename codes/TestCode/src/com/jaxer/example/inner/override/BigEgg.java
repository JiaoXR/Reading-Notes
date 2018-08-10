package com.jaxer.example.inner.override;

/**
 * Created by jxr on 3:57 PM
 * 内部类可以被覆盖吗？这两个内部类是完全独立的两个实体
 */
public class BigEgg extends Egg {
    public class Yolk {
        public Yolk() {
            System.out.println("BigEgg.Yolk()");
        }
    }

    public static void main(String[] args) {
        new BigEgg();
    }
}
