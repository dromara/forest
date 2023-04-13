package org.dromara.forest.converter.text;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.exceptions.ForestConvertException;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.body.NameValueRequestBody;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtils;

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
    public byte[] encodeRequestBody(ForestBody body, Charset charset, ConvertOptions options) {
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
