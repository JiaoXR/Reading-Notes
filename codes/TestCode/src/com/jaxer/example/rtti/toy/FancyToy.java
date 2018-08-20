package com.jaxer.example.rtti.toy;

/**
 * Created by jxr on 3:44 PM
 */
public class FancyToy extends Toy implements HasBatteries, Waterproof, Shoots {
    public FancyToy() {
        super(1);
    }
}
