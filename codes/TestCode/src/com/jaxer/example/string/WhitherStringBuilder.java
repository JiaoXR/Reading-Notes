package com.jaxer.example.string;

/**
 * Created by jxr on 2:32 PM
 */
public class WhitherStringBuilder {
    /**
     * 隐式使用 StringBuilder
     * 该方式会创建多个 StringBuilder 对象
     */
    public String implicit(String[] fields) {
        String result = "";
        for (String field : fields) {
            result += field;
        }
        return result;
    }

    /**
     * 显式使用 StringBuilder
     * 只生产一个 StringBuilder 对象；且允许预先指定 StringBuilder 的大小，避免多次重新分配缓冲区
     */
    public String explicit(String[] fields) {
        StringBuilder result = new StringBuilder(fields.length);
        for (String field : fields) {
            result.append(field);
        }
        return result.toString();
    }
}
