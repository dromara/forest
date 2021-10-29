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

package com.dtflys.forest.converter.json;

import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;
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

    protected ObjectMapper mapper = new ObjectMapper();
    {
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
            DateFormat format = new SimpleDateFormat(dateFormat);
            mapper.setDateFormat(format);
        }
    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }

    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        try {
            String str = StringUtils.fromBytes(source, charset);
            return mapper.readValue(str, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        try {
            String str = StringUtils.fromBytes(source, charset);
            return mapper.readValue(str, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public <T> T convertToJavaObject(String source, Class<?> parametrized, Class<?> ...parameterClasses) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public <T> T convertToJavaObject(String source, JavaType javaType) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        try {
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public String encodeToString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public Map<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            Map objMap = (Map) obj;
            Map<String, Object> newMap = new HashMap<>(objMap.size());
            for (Object key : objMap.keySet()) {
                Object val = objMap.get(key);
                if (val != null) {
                    newMap.put(String.valueOf(key), val);
                }
            }
            return newMap;
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }

        JavaType javaType = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        return mapper.convertValue(obj, javaType);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }
}
