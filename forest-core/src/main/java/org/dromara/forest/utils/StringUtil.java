package org.dromara.forest.utils;

import okio.ByteString;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-17
 */
public final class StringUtil {

    private StringUtil() {}

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence text) {
        return !isEmpty(text);
    }

    /**
     * 是否为空白字符串
     * <p>字符串为null，空串，或都是空格的情况为 {@code true}
     *
     * @param text 字符串
     * @return {@code true}: 是空白字符串, {@code false}: 不是空白字符串
     */
    public static boolean isBlank(String text) {
        if (text == null) {
            return true;
        }
        if (text.length() == 0) {
            return true;
        }
        char[] chars = text.toCharArray();
        int len = chars.length;
        for (int i = 0; i < len; ++i) {
            if (!Character.isWhitespace(chars[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为非空白字符串，即 {@link StringUtil#isBlank(String)}的逆否命题
     * <p>字符串为null，空串，或都是空格的情况为 {@code false}
     *
     * @param text 字符串
     * @return {@code true}: 不是空白字符串, {@code false}: 是空白字符串
     */
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

    /**
     * 生成 boundary 字符串
     * @return boundary 字符串
     */
    public static String generateBoundary() {
        UUID uuid = UUID.randomUUID();
        return ByteString.encodeUtf8(uuid.toString()).utf8();
    }
}
