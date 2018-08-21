package com.jaxer.example.rtti.empty;

/**
 * Created by jxr on 11:08 AM
 * 空对象
 */
public class Person {
    private final String first;
    private final String last;
    private final String address;

    Person(String first, String last, String address) {
        this.first = first;
        this.last = last;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Person: " + first + " " + last + " " + address;
    }

    public static class NullPerson extends Person implements Null {
        private NullPerson() {
            super("None", "None", "None");
        }

        @Override
        public String toString() {
            return "NullPerson";
        }
    }

    public static final Person NULL = new NullPerson();
}
