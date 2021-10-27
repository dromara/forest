package com.dtflys.forest.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

public class Validations {

    public static void assertParamNotNull(Object obj, String paramName) {
        if (obj == null) {
            throwParameterEmptyException(paramName);
        }
    }


    public static void assertParamNotEmpty(Object obj, String paramName) {
        if (ObjectUtils.isEmpty(obj)) {
            throwParameterEmptyException(paramName);
        }
    }

    public static void assertParamNotEmpty(String str, String paramName) {
        if (StringUtils.isEmpty(str)) {
            throwParameterEmptyException(paramName);
        }
    }

    public static void assertParamNotEmpty(Collection collection, String paramName) {
        if (CollectionUtils.isEmpty(collection)) {
            throwParameterEmptyException(paramName);
        }
    }


    private static void throwParameterEmptyException(String paramName) {
        throw new IllegalArgumentException("parameter '" + paramName + "' is required. it can not be empty.");
    }
}
