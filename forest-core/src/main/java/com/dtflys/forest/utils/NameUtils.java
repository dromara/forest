package com.dtflys.forest.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-10 22:48
 */
public class NameUtils {

    /**
     * 按驼峰命名法的规则将字符串分割
     * @param name
     * @return
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

}
