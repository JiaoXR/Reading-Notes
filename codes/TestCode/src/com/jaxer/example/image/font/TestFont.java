package com.jaxer.example.image.font;

import java.awt.*;

/**
 * Created by jxr on 4:19 PM 2018/10/14
 */
public class TestFont {
    public static void main(String[] args) {
        getSystemFonts();
    }

    /**
     * 获取系统支持的字体
     */
    private static void getSystemFonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = e.getAvailableFontFamilyNames();
        for (String fontName : fontNames) {
            System.out.println(fontName);
        }
    }

}
