package com.dtflys.forest.converter.auto;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.protobuf.ForestProtobufConverterManager;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class DefaultAutoConverter implements ForestConverter<Object> {

    private final ForestConfiguration configuration;

    private final ForestProtobufConverterManager protobufConverterManager = ForestProtobufConverterManager.getInstance();

    public DefaultAutoConverter(ForestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T convertToJavaObject(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        if (source instanceof InputStream
                || source instanceof byte[]
                || source instanceof File) {
            if (canReadAsBinary(targetType)) {
                return tryConvert(source, targetType, ForestDataType.BINARY);
            }
            if (protobufConverterManager.isProtobufMessageClass(targetType)) {
                return tryConvert(source, targetType, ForestDataType.PROTOBUF);
            }
            source = readAsString(source);
        }
        T result = null;
        if (source instanceof CharSequence) {
            String str = source.toString();
            if (String.class.isAssignableFrom(targetType)) {
                return (T) str;
            }
            String trimmedStr = str.trim();
            if (trimmedStr.length() == 0) {
                if (CharSequence.class.isAssignableFrom(targetType)) {
                    return tryConvert(str, targetType, ForestDataType.TEXT);
                }
                return null;
            }
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
                } else if ("true".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
                        result = (T) Boolean.TRUE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, ForestDataType.TEXT);
                    }
                } else if ("false".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType)) {
                        result = (T) Boolean.FALSE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, ForestDataType.TEXT);
                    }
                } else {
                    result = tryConvert(source, targetType, ForestDataType.TEXT);
                }
            } catch (Throwable th) {
                try {
                    result = tryConvert(trimmedStr, targetType, ForestDataType.TEXT);
                } catch (Throwable th2) {
                    throw new ForestConvertException(this, th2);
                }
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
        if (source == null) {
            return null;
        }
        if (source instanceof InputStream
                || source instanceof byte[]
                || source instanceof File) {
            if (canReadAsBinary(targetType)) {
                return tryConvert(source, targetType, ForestDataType.BINARY);
            }
            if (protobufConverterManager.isProtobufMessageType(targetType)) {
                return tryConvert(source, targetType, ForestDataType.PROTOBUF);
            }
            source = readAsString(source);
        }
        T result = null;
        Class clazz = ReflectUtils.toClass(targetType);
        if (source instanceof CharSequence) {
            String str = source.toString();
            if (String.class.isAssignableFrom(clazz)) {
                return (T) str;
            }
            String trimmedStr = str.trim();
            if (trimmedStr.length() == 0) {
                if (CharSequence.class.isAssignableFrom(clazz)) {
                    return tryConvert(str, targetType, ForestDataType.TEXT);
                }
                return null;
            }
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
                } else if ("true".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
                        result = (T) Boolean.TRUE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, ForestDataType.TEXT);
                    }
                } else if ("false".equalsIgnoreCase(trimmedStr)) {
                    if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
                        result = (T) Boolean.FALSE;
                    } else {
                        result = tryConvert(trimmedStr, targetType, ForestDataType.TEXT);
                    }
                } else {
                    result = tryConvert(source, targetType, ForestDataType.TEXT);
                }
            } catch (Throwable th) {
                throw new ForestConvertException(this, th);
            }
        }
        return result;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        return convertToJavaObject((Object) source, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        return convertToJavaObject((Object) source, targetType);
    }


    private boolean canReadAsBinary(Class targetType) {
        if (byte[].class.isAssignableFrom(targetType)
                || InputStream.class.isAssignableFrom(targetType)
                || File.class.isAssignableFrom(targetType)) {
            return true;
        }
        return false;
    }

    private boolean canReadAsBinary(Type targetType) {
        Class type = ReflectUtils.toClass(targetType);
        if (byte[].class.isAssignableFrom(type)
                || InputStream.class.isAssignableFrom(type)
                || File.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }


    public String readAsString(Object source) {
        if (source instanceof byte[]) {
            return bytesToString((byte[]) source);
        }
        if (source instanceof InputStream) {
            return inputStreamToString((InputStream) source);
        }
        if (source instanceof File) {
            return fileToString((File) source);
        }
        throw new ForestRuntimeException("[Forest] cannot read as string from instance of class '" + source.getClass().getName() + "'");
    }

    private String bytesToString(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            return IOUtils.toString(byteArrayInputStream);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public String fileToString(File file) {
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.AUTO;
    }

}
