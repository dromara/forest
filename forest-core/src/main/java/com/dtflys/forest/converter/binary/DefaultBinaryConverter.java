package com.dtflys.forest.converter.binary;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ForestProgress;

import java.io.*;
import java.lang.reflect.Type;

public class DefaultBinaryConverter implements ForestConverter<InputStream> {


    @Override
    public <T> T convertToJavaObject(InputStream source, Class<T> targetType) {
        if (InputStream.class.isAssignableFrom(targetType)) {
            return (T) source;
        }
        if (byte[].class.isAssignableFrom(targetType)) {
            return (T) inputStreamToByteArray(source);
        }
        if (String.class.isAssignableFrom(targetType)) {
            byte[] tmp = inputStreamToByteArray(source);
            String result = new String(tmp);
            return (T) result;
        }
        return convertToJavaObjectEx(source, targetType);
    }


    protected <T> T convertToJavaObjectEx(InputStream source, Class<T> targetType) {
        return null;
    }


    private byte[] inputStreamToByteArray(InputStream source) {
        byte[] tmp = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int len;
            while((len = source.read(tmp)) != -1) {
                out.write(tmp, 0, len);
            }
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        } finally {
            try {
                source.close();
            } catch (IOException e) {
                throw new ForestRuntimeException(e);
            }
        }
    }

    @Override
    public <T> T convertToJavaObject(InputStream source, Type targetType) {
        return null;
    }

}
