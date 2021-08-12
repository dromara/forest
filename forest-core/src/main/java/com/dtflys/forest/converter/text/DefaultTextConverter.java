package com.dtflys.forest.converter.text;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        String str = StringUtils.fromBytes(source, charset);
        try {
            return (T) str;
        } catch (Throwable th) {
            throw new ForestConvertException("text", th);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        String str = StringUtils.fromBytes(source, charset);
        try {
            return (T) str;
        } catch (Throwable th) {
            throw new ForestConvertException("text", th);
        }
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.TEXT;
    }
}
