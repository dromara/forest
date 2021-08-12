package com.dtflys.forest.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    /**
     * 去掉开头空格
     * @param str 源字符串
     * @return 去掉开头空格后的字符串
     */
    public static String trimBegin(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int len = str.length();
        boolean before = true;
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (before) {
                if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                    continue;
                }
            }
            builder.append(ch);
            before = false;
        }
        return builder.toString();
    }

    public static String fromBytes(byte[] bytes, Charset charset, Charset defaultCharset) {
        if (charset == null) {
            charset = defaultCharset;
        }
        return new String(bytes, charset);
    }


    public static String fromBytes(byte[] bytes, Charset charset) {
        return fromBytes(bytes, charset, StandardCharsets.UTF_8);
    }
}
