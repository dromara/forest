package com.dtflys.forest.converter;

import java.lang.reflect.Type;

/**
 * Created by Gongjun on 2016/5/26.
 */
public interface ForestConverter {

    <T> T convertToJavaObject(String source, Class<T> targetType);

    <T> T convertToJavaObject(String source, Type targetType);
}
