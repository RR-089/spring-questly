package com.personal.spring_questly.utils;

import java.lang.reflect.Field;

public class FieldUtils {
    public static void injectField(Object target, String field, Object value) {
        try {
            Field declaredField = target.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Cannot inject field");
        }
    }
}
