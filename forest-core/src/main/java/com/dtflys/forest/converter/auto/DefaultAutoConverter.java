package com.dtflys.forest.converter.auto;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;

public class DefaultAutoConverter implements ForestConverter<Object> {

    private final ForestConfiguration configuration;

    public DefaultAutoConverter(ForestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        if (source instanceof InputStream
                || source instanceof byte[]
                || source instanceof File) {
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
                    result = tryConvert(trimmedStr, targetType, ForestDataType.JSON);
                } else if (ch == '<') {
                    result = tryConvert(trimmedStr, targetType, ForestDataType.XML);
                } else if (Character.isDigit(ch)) {
                    try {
                        result = tryConvert(trimmedStr, targetType, ForestDataType.JSON);
                    } catch (Throwable th) {
                        result = tryConvert(source, targetType, ForestDataType.TEXT);
                    }
                } else {
                    result = tryConvert(source, targetType, ForestDataType.TEXT);
                }
            } catch (Throwable th) {
                throw new ForestRuntimeException(th);
            }
        }
        return result;
    }

    private <T> T tryConvert(Object source, Class<T> targetType, ForestDataType dataType) {
        return (T) configuration.getConverterMap().get(dataType).convertToJavaObject(source, targetType);
    }

    private <T> T tryConvert(Object source, Type targetType, ForestDataType dataType) {
        return (T) configuration.getConverterMap().get(dataType).convertToJavaObject(source, targetType);
    }


    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        if (source instanceof InputStream
                || source instanceof byte[]
                || source instanceof File) {
            return tryConvert(source, targetType, ForestDataType.BINARY);
        }
        T result = null;
        Class clazz = ReflectUtils.getClassByType(targetType);
        if (source instanceof CharSequence) {
            String str = source.toString();
            if (String.class.isAssignableFrom(clazz)) {
                return (T) str;
            }
            String trimmedStr = str.trim();
            char ch = trimmedStr.charAt(0);
            try {
                if (ch == '{' || ch == '[') {
                    result = tryConvert(trimmedStr, targetType, ForestDataType.JSON);
                } else if (ch == '<') {
                    result = tryConvert(trimmedStr, targetType, ForestDataType.XML);
                } else if (Character.isDigit(ch)) {
                    try {
                        result = tryConvert(trimmedStr, targetType, ForestDataType.JSON);
                    } catch (Throwable th) {
                        result = tryConvert(source, targetType, ForestDataType.TEXT);
                    }
                } else {
                    result = tryConvert(source, targetType, ForestDataType.TEXT);
                }
            } catch (Throwable th) {
                result = tryConvert(source, targetType, ForestDataType.TEXT);
            }
        }
        return result;
    }

}
