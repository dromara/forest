package com.dtflys.forest.converter.binary;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.utils.ReflectUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;

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
                String result = new String(tmp);
                return (T) result;
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
        byte[] tmp = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int len;
            while((len = in.read(tmp)) != -1) {
                out.write(tmp, 0, len);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new ForestConvertException("binary", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new ForestConvertException("binary", e);
            }
        }
    }

    @Override
    public <T> T convertToJavaObject(Object source, Type targetType) {
        Class clazz = ReflectUtils.getClassByType(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

}
