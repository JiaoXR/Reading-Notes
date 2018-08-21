package com.jaxer.example.collection.advanced;

/**
 * Created by jxr on 4:01 PM
 */
public class StringAddress {
    private String string;

    public StringAddress(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return super.toString() + " " + string;
    }
}
