package com.dtflys.forest.converter.text;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.utils.ForestDataType;

import java.lang.reflect.Type;

public class DefaultTextConverter implements ForestConverter<String> {
    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        return (T) source;
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        return (T) source;
    }

    @Override
    public ForestDataType getDateType() {
        return ForestDataType.TEXT;
    }
}
