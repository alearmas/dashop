package com.aarmas.sales_service.utils;

public class EnumUtils {

    public static <T extends Enum<T>> T safeEnum(Class<T> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Valor inválido para enum " + enumClass.getSimpleName() + ": " + value);
        }
    }

}