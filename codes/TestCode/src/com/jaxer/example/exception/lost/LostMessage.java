package com.jaxer.example.exception.lost;

/**
 * Created by jxr on 10:32 AM
 * 异常丢失
 */
public class LostMessage {
    private static void f() throws VeryImportantException {
        throw new VeryImportantException();
    }

    private static void dispose() throws HoHumException {
        throw new HoHumException();
    }

    public static void main(String[] args) {
        try {
            try {
                f();
            } finally {
                dispose();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
