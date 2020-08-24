package com.dtflys.forest.converter;

import java.lang.reflect.Type;

/**
 * Created by Gongjun on 2016/5/26.
 */
public interface ForestConverter<S> {

    <T> T convertToJavaObject(S source, Class<T> targetType);

    <T> T convertToJavaObject(S source, Type targetType);
}
