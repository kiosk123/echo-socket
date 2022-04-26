package com.common;

public class ContextUtil {
    private static ThreadLocal<String> CONTEXT = new ThreadLocal<String>();

    public static void setValue(String value) {
        CONTEXT.set(value);
    }

    public static String getValue() {
        return CONTEXT.get();
    }
}
