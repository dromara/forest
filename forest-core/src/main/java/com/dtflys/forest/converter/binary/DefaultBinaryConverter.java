package com.dtflys.forest.converter.binary;

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.ByteEncodeUtils;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * 默认的二进制数据转换器
 *
 * @author gongjun
 * @since 2020-08-03 20:05
 */
public class DefaultBinaryConverter implements ForestConverter<Object>, ForestEncoder {

    private DefaultAutoConverter autoConverter;

    public DefaultBinaryConverter(DefaultAutoConverter autoConverter) {
        this.autoConverter = autoConverter;
    }


    private <T> T convertToJavaObject(final Object source, final Class<T> targetType, final Charset charset) {
        final Object src = source instanceof byte[] ? new ByteArrayInputStream((byte[]) source) : source;
        if (src instanceof InputStream) {
            final InputStream in = (InputStream) src;
            if (InputStream.class.isAssignableFrom(targetType)) {
                return (T) src;
            }
            if (byte[].class.isAssignableFrom(targetType)) {
                return (T) inputStreamToByteArray(in);
            }
            final byte[] tmp = inputStreamToByteArray(in);
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
        } else if (src instanceof File) {
            final File file = (File) src;
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
                final String str = FileUtils.readFileToString(file);
                if (String.class.isAssignableFrom(targetType)) {
                    return (T) str;
                }
                return autoConverter.convertToJavaObject(str, targetType);
            } catch (IOException e) {
                throw new ForestConvertException(this, e);
            }
        }
        return convertToJavaObjectEx(src, targetType);

    }

    @Override
    public <T> T convertToJavaObject(final Object source, final Class<T> targetType) {
        return convertToJavaObject(source, targetType, StandardCharsets.UTF_8);
    }


    protected <T> T convertToJavaObjectEx(final Object source, final Class<T> targetType) {
        return convertToJavaObject(source, targetType, StandardCharsets.UTF_8);
    }


    private byte[] inputStreamToByteArray(final InputStream in) {
        try {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public <T> T convertToJavaObject(final Object source, final Type targetType) {
        final Class clazz = ReflectUtils.toClass(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Class<T> targetType, final Charset charset) {
        return convertToJavaObject((Object) source, targetType, StandardCharsets.UTF_8);
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Type targetType, final Charset charset) {
        final Class clazz = ReflectUtils.toClass(targetType);
        return (T) convertToJavaObject((Object) source, clazz, StandardCharsets.UTF_8);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.BINARY;
    }

    @Override
    public byte[] encodeRequestBody(final ForestBody reqBody, final Charset charset, final ConvertOptions options) {
        final List<byte[]> byteList = new LinkedList<>();
        int size = 0;
        for (final ForestRequestBody body : reqBody) {
            final byte[] byteArray = body.getByteArray();
            byteList.add(byteArray);
            size += byteArray.length;
        }
        final byte[] bytes = new byte[size];
        int pos = 0;
        for (final byte[] bytesItem : byteList) {
            for (int i = 0; i < bytesItem.length; i++) {
                bytes[pos + i] = bytesItem[i];
            }
            pos += bytesItem.length;
        }
        return bytes;
    }

    @Override
    public byte[] encodeRequestBody(final ForestRequest request, final Charset charset) {
        final ForestBody reqBody = request.body();
        final List<ForestMultipart> multiparts = request.getMultiparts();
        final List<byte[]> byteList = new LinkedList<>();
        int size = 0;
        for (final ForestMultipart multipart : multiparts) {
            final byte[] byteArray = multipart.getBytes();
            byteList.add(byteArray);
            size += byteArray.length;
        }
        for (final ForestRequestBody body : reqBody) {
            final byte[] byteArray = body.getByteArray();
            byteList.add(byteArray);
            size += byteArray.length;
        }
        final byte[] bytes = new byte[size];
        int pos = 0;
        for (final byte[] bytesItem : byteList) {
            for (int i = 0; i < bytesItem.length; i++) {
                bytes[pos + i] = bytesItem[i];
            }
            pos += bytesItem.length;
        }
        return bytes;
    }
}
