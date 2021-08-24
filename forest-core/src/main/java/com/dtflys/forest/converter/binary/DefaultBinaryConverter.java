package com.dtflys.forest.converter.binary;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import scala.Char;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 默认的二进制数据转换器
 *
 * @author gongjun
 * @since 2020-08-03 20:05
 */
public class DefaultBinaryConverter implements ForestConverter<Object> {

    private DefaultAutoConverter autoConverter;

    public DefaultBinaryConverter(DefaultAutoConverter autoConverter) {
        this.autoConverter = autoConverter;
    }


    private <T> T convertToJavaObject(Object source, Class<T> targetType, Charset charset) {
        if (source instanceof byte[]) {
            source = new ByteArrayInputStream((byte[]) source);
        }
        if (source instanceof InputStream) {
            InputStream in = (InputStream) source;
            if (InputStream.class.isAssignableFrom(targetType)) {
                return (T) source;
            }
            if (byte[].class.isAssignableFrom(targetType)) {
                return (T) inputStreamToByteArray(in);
            }
            byte[] tmp = inputStreamToByteArray(in);
            String str = null;
            try {
                String encode;
                if (charset == null) {
                    encode = ByteEncodeUtils.getCharsetName(tmp);
                    if (encode.toUpperCase().startsWith("GB")) {
                        encode = "GBK";
                    }
                } else {
                    encode = charset.name();
                }
                str = IOUtils.toString(tmp, encode);
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
            if (String.class.isAssignableFrom(targetType)) {
                return (T) str;
            }
            return autoConverter.convertToJavaObject(str, targetType);
        } else if (source instanceof File) {
            File file = (File) source;
            if (File.class.isAssignableFrom(targetType)) {
                return (T) file;
            }
            try {
                if (InputStream.class.isAssignableFrom(targetType)) {
                    return (T) FileUtils.openInputStream(file);
                }
                if (byte[].class.isAssignableFrom(targetType)) {
                    return (T) FileUtils.readFileToByteArray(file);
                }
                String str = FileUtils.readFileToString(file);
                if (String.class.isAssignableFrom(targetType)) {
                    return (T) str;
                }
                return autoConverter.convertToJavaObject(str, targetType);
            } catch (IOException e) {
                throw new ForestConvertException(this, e);
            }
        }
        return convertToJavaObjectEx(source, targetType);

    }

    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        return convertToJavaObject(source, targetType, StandardCharsets.UTF_8);
    }


    protected <T> T convertToJavaObjectEx(Object source, Class<T> targetType) {
        return convertToJavaObject(source, targetType, StandardCharsets.UTF_8);
    }


    private byte[] inputStreamToByteArray(InputStream in) {
        try {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        Class clazz = ReflectUtils.getClassByType(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        return convertToJavaObject((Object) source, targetType, StandardCharsets.UTF_8);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        Class clazz = ReflectUtils.getClassByType(targetType);
        return (T) convertToJavaObject((Object) source, clazz, StandardCharsets.UTF_8);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.BINARY;
    }

}
