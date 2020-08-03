package com.dtflys.forest.converter.auto;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.utils.ForestDataType;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class DefaultAutoConverter implements ForestConverter<Object> {

    private final ForestConfiguration configuration;

    public DefaultAutoConverter(ForestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        if (source instanceof InputStream) {
            return tryConvert(source, targetType, ForestDataType.BINARY);
        }
        T result = null;
        if (source instanceof CharSequence) {
            String str = source.toString();
            if (String.class.isAssignableFrom(targetType)) {
                return (T) str;
            }
            String trimmedStr = str.trim();
            char ch = trimmedStr.charAt(0);
            try {
                if (ch == '{' || ch == '[') {
                    result = tryConvert(source, targetType, ForestDataType.JSON);
                } else if (ch == '<') {
                    result = tryConvert(source, targetType, ForestDataType.XML);
                } else if (Character.isDigit(ch)) {
                    result = tryConvert(source, targetType, ForestDataType.JSON);
                } else {
                    result = tryConvert(source, targetType, ForestDataType.TEXT);
                }
            } catch (Throwable th) {
                result = tryConvert(source, targetType, ForestDataType.TEXT);
            }
        }
        return result;
    }

    private <T> T tryConvert(Object source, Class<T> targetType, ForestDataType dataType) {
        return (T) configuration.getConverterMap().get(dataType).convertToJavaObject(source, targetType);
    }

    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        return null;
    }

}
