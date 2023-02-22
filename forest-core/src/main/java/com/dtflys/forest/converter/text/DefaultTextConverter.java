package com.dtflys.forest.converter.text;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultTextConverter implements ForestConverter<String>, ForestEncoder {

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
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        String str = StringUtils.fromBytes(source, charset);
        try {
            return (T) str;
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.TEXT;
    }

    @Override
    public String encodeToString(Object obj) {
        return null;
    }

    @Override
    public byte[] encodeRequestBody(ForestBody body, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        StringBuilder builder = new StringBuilder();
        ForestRequestBody lastBodyItem = null;
        for (ForestRequestBody bodyItem : body) {
            if (lastBodyItem != null && lastBodyItem instanceof NameValueRequestBody) {
                builder.append("&");
            }
            builder.append(bodyItem.toString());
            lastBodyItem = bodyItem;
        }
        String strBody = builder.toString();
        byte[] bytes = strBody.getBytes(charset);
        return bytes;
    }
}
