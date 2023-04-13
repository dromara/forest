package org.dromara.forest.utils;


import java.util.Collection;
import java.util.Map;

public class Validations {

    public static void assertParamNotNull(Object obj, String paramName) {
        if (obj == null) {
            throwParameterEmptyException(paramName);
        }
    }

    public static void assertParamNotEmpty(Object obj, String paramName) {
        if (obj == null) {
            throwParameterEmptyException(paramName);
        }
        if (obj instanceof Collection && ((Collection<?>) obj).isEmpty()) {
            throwParameterEmptyException(paramName);
        }
        if (obj instanceof Map && ((Map<?, ?>) obj).isEmpty()) {
            throwParameterEmptyException(paramName);
        }
    }

    public static void assertParamNotEmpty(String str, String paramName) {
        if (StringUtils.isEmpty(str)) {
            throwParameterEmptyException(paramName);
        }
    }

    public static void assertParamNotEmpty(Collection collection, String paramName) {
        if (collection == null || collection.isEmpty()) {
            throwParameterEmptyException(paramName);
        }
    }


    private static void throwParameterEmptyException(String paramName) {
        throw new IllegalArgumentException("parameter '" + paramName + "' is required. it can not be empty.");
    }
}
