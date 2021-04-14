package com.dtflys.forest.converter.binary;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.ReflectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;

/**
 * 默认的二进制数据转换器
 *
 * @author gongjun
 * @since 2020-08-03 20:05
 */
public class DefaultBinaryConverter implements ForestConverter<Object> {


    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        if (source instanceof InputStream) {
            InputStream in = (InputStream) source;
            if (InputStream.class.isAssignableFrom(targetType)) {
                return (T) source;
            }
            if (byte[].class.isAssignableFrom(targetType)) {
                return (T) inputStreamToByteArray(in);
            }
            if (String.class.isAssignableFrom(targetType)) {
                byte[] tmp = inputStreamToByteArray(in);
                String result = null;
                try {
                    String encode = ByteEncodeUtils.getCharsetName(tmp);
                    if (encode.toUpperCase().startsWith("GB")) {
                        encode = "GBK";
                    }
                    result = IOUtils.toString(tmp, encode);
                    return (T) result;
                } catch (IOException e) {
                    throw new ForestRuntimeException(e);
                }
            }
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
                if (String.class.isAssignableFrom(targetType)) {
                    return (T) FileUtils.readFileToString(file);
                }
            } catch (IOException e) {
                throw new ForestConvertException("binary", e);
            }
        }
        return convertToJavaObjectEx(source, targetType);
    }


    protected <T> T convertToJavaObjectEx(Object source, Class<T> targetType) {
        return null;
    }


    private byte[] inputStreamToByteArray(InputStream in) {
        try {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new ForestConvertException("binary", e);
        }
    }

    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        Class clazz = ReflectUtils.getClassByType(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

}
