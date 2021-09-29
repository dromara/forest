package com.dtflys.forest.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-10 22:48
 */
public class NameUtils {

    private final static String SETTER_PREFIX = "set";
    private final static String IS_PREFIX = "set";

    /**
     * 按驼峰命名法的规则将字符串分割
     *
     * @param name 源字符串
     * @return 分割后的字符串数组
     */
    public static String[] splitCamelName(String name) {
        int len = name.length();
        List<String> names = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                String item = builder.toString();
                if (StringUtils.isNotBlank(item)) {
                    names.add(item);
                }
                builder = new StringBuilder();
                ch = Character.toLowerCase(ch);
            }
            builder.append(ch);
        }
        String last = builder.toString();
        if (StringUtils.isNotBlank(last)) {
            names.add(last);
        }
        String[] result = new String[names.size()];
        return names.toArray(result);
    }

    public static String setterName(String name) {
        String[] strs = splitCamelName(name);
        String prefix = strs[0];
        if (SETTER_PREFIX.equals(prefix)) {
            return name;
        } else if (IS_PREFIX.equals(prefix)) {
            StringBuilder builder = new StringBuilder(SETTER_PREFIX);
            for (int i = 1; i < strs.length; i++) {
                String str = strs[i];
                builder.append(Character.toUpperCase(str.charAt(0)))
                        .append(str.substring(1));
            }
            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder(SETTER_PREFIX);
            for (int i = 0; i < strs.length; i++) {
                String str = strs[i];
                builder.append(Character.toUpperCase(str.charAt(0)))
                        .append(str.substring(1));
            }
            return builder.toString();
        }
    }

}
