/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.forest.converter.json;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.exceptions.ForestConvertException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.Lazy;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 使用Jackson实现的消息转折实现类
 * @author Gongjun
 * @since 2016-06-04
 */
public class ForestJacksonConverter implements ForestJsonConverter {

    /** 日期格式 */
    private String dateFormat;

    protected ObjectMapper mapper;

    public ForestJacksonConverter() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }

    /**
     * 获取Jackson的Mapper对象
     * @return Jackson的Mapper对象，{@link ObjectMapper}类实例
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        if (StringUtils.isNotBlank(dateFormat)) {
            final DateFormat format = new SimpleDateFormat(dateFormat);
            getMapper().setDateFormat(format);
        }
    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return getMapper().readValue(source, getMapper().getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }

    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        try {
            final String str = StringUtils.fromBytes(source, charset);
            return getMapper().readValue(str, getMapper().getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        try {
            final String str = StringUtils.fromBytes(source, charset);
            return getMapper().readValue(str, getMapper().getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public <T> T convertToJavaObject(String source, Class<?> parametrized, Class<?> ...parameterClasses) {
        try {
            final JavaType javaType = getMapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return getMapper().readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public <T> T convertToJavaObject(String source, JavaType javaType) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        try {
            return getMapper().readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public String encodeToString(Object obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (Throwable e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public Map<String, Object> convertObjectToMap(Object obj, ForestRequest request, ConvertOptions options) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            final Map objMap = (Map) obj;
            final Map<String, Object> newMap = new HashMap<>(objMap.size());
            for (Object key : objMap.keySet()) {
                final String name = String.valueOf(key);
                if (options != null && options.shouldExclude(name)) {
                    continue;
                }
                Object val = objMap.get(key);
                if (Lazy.isEvaluatingLazyValue(val, request)) {
                    continue;
                }
                if (options != null) {
                    val = options.getValue(val, request);
                    if (options.shouldIgnore(val)) {
                        continue;
                    }
                }
                if (val != null) {
                    newMap.put(name, val);
                }
            }
            return newMap;
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }

        final JavaType javaType = getMapper().getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        return getMapper().convertValue(obj, javaType);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }
}
