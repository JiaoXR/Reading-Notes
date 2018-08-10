package com.jaxer.example.inner.override;

/**
 * Created by jxr on 3:46 PM
 */
public class Egg {
    private Yolk yolk;

    protected class Yolk {
        public Yolk() {
            System.out.println("Egg.Yolk()");
        }
    }

    public Egg() {
        System.out.println("New Egg()");
        yolk = new Yolk();
    }
}
