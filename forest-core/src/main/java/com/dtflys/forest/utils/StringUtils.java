package com.dtflys.forest.utils;

import java.lang.reflect.Method;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-17
 */
public final class StringUtils {

    private StringUtils() {}

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    public static boolean isBlank(String text) {
        if (text == null) {
            return true;
        }
        int strLen = text.length();
        if (text != null && strLen != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(text.charAt(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    public static String getGetterName(Method mtd) {
        String name = mtd.getName();
        if (name.startsWith("get")) {
            return Character.toLowerCase(name.charAt(3)) + name.substring(4);
        }
        if (name.startsWith("is")) {
            return Character.toLowerCase(name.charAt(2)) + name.substring(3);
        }
        return null;
    }

    public static String toGetterName(String name) {
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }



}
