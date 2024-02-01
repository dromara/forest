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


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.codec.FieldInfo;
import com.alibaba.fastjson2.util.BeanUtils;
import com.alibaba.fastjson2.util.TypeUtils;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.NameUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * 使用Fastjson实现的消息转换实现类
 * @author gongjun
 * @since 2016-05-30
 */
public class ForestFastjson2Converter implements ForestJsonConverter {

    private final static int PARSE_LIMIT = 1024 * 1024;

/**
     * Fastjson序列化方式
     */




    private List<JSONWriter.Feature> writerFeatures = new LinkedList<>();

    private List<JSONReader.Feature> readFeatures = new LinkedList<>();

    /** 日期格式 */
    private String dateFormat;


    private static Field nameField;

    private static Method nameMethod;

    static {
        Class<?> clazz = FieldInfo.class;
        try {
            nameField = clazz.getField("name");
        } catch (NoSuchFieldException e) {
            try {
                nameMethod = clazz.getMethod("getName", new Class[0]);
            } catch (NoSuchMethodException ex) {
            }
        }
    }


    public List<JSONWriter.Feature> getWriterFeatures() {
        return writerFeatures;
    }

    public List<JSONReader.Feature> getReadFeatures() {
        return readFeatures;
    }

    public ForestFastjson2Converter() {
    }

    public void addWriterFeature(JSONWriter.Feature feature) {
        this.writerFeatures.add(feature);
    }

    public void addReadFeature(JSONReader.Feature feature) {
        this.readFeatures.add(feature);
    }

    private JSONReader.Feature[] getReadFeatureArray() {
        return readFeatures.toArray(new JSONReader.Feature[0]);
    }

    private JSONWriter.Feature[] getWriterFeatureArray() {
        return writerFeatures.toArray(new JSONWriter.Feature[0]);
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType, getReadFeatureArray());
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        try {
            return JSON.parseObject(source, 0, source.length, charset, targetType, readFeatures.toArray(new JSONReader.Feature[0]));
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        try {
            return JSON.parseObject(source, 0, source.length, charset, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }


    private String parseToString(Object obj) {
        return JSON.toJSONString(obj, dateFormat, getWriterFeatureArray());

    }

    @Override
    public String encodeToString(Object obj) {
        if (obj instanceof CharSequence) {
            obj.toString();
        }
        try {
            return parseToString(obj);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
    }



    @Override
    public Map<String, Object> convertObjectToMap(Object obj, ForestRequest request, ConvertOptions options) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            final Map objMap = (Map) obj;
            final Map<String, Object> newMap = new LinkedHashMap<>(objMap.size());
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
        return  (JSONObject) Optional.ofNullable(JSON.toJSON(obj)).orElse(new JSONObject());


/*
        final Map<String, Object> map = new LinkedHashMap<>();
        final Class<?> objClass = obj.getClass();
        final Object[] args = new Object[0];

        BeanUtils.getters(objClass, getter -> {
            final String methodName = getter.getName();
            final Class<?> propType = getter.getReturnType();
            final String propName = NameUtils.propNameFromGetter(methodName);
            if (options != null && options.shouldExclude(propName)) {
                return;
            }
            Object value = null;
            try {
                value = getter.invoke(obj, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            if (options != null) {
                value = options.getValue(value, request);
                if (options.shouldIgnore(value)) {
                    return;
                }
            }
            if (ReflectUtils.isPrimaryArrayType(propType)) {
                final Object jsonValue = JSON.toJSON(value);
                map.put(propName, jsonValue);
            } else {
                map.put(propName, value);
            }
        });
        return map;
*/

    }

    @Override
    public void setDateFormat(String format) {
        this.dateFormat = format;
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }
}
