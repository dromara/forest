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

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;
import com.fasterxml.jackson.databind.util.BeanUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 使用Fastjson实现的消息转换实现类
 * @author gongjun
 * @since 2016-05-30
 */
public class ForestFastjsonConverter implements ForestJsonConverter {

    /**
     * Fastjson序列化方式
     */
    private String serializerFeatureName = "DisableCircularReferenceDetect";

    private SerializerFeature serializerFeature;

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

    /**
     * 获取FastJson的序列化特性名
     * @return FastJson的序列化特性名字符串
     */
    public String getSerializerFeatureName() {
        return serializerFeatureName;
    }

    /**
     * 设置FastJson的序列化特性名
     * @param serializerFeatureName FastJson的序列化特性名字符串
     */
    public void setSerializerFeatureName(final String serializerFeatureName) {
        if (StringUtils.isNotBlank(serializerFeatureName)) {
            this.serializerFeatureName = serializerFeatureName;
            final SerializerFeature feature = SerializerFeature.valueOf(serializerFeatureName);
            setSerializerFeature(feature);
        }
    }

    /**
     * 获取FastJson的序列化特性对象
     * @return FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public SerializerFeature getSerializerFeature() {
        return serializerFeature;
    }

    public ForestFastjsonConverter() {
        setSerializerFeature(SerializerFeature.valueOf(serializerFeatureName));
    }

    /**
     * 设置FastJson的序列化特性对象
     * @param serializerFeature FastJson的序列化特性对象，{@link SerializerFeature}枚举实例
     */
    public void setSerializerFeature(final SerializerFeature serializerFeature) {
        this.serializerFeature = serializerFeature;
        if (serializerFeature == null) {
            this.serializerFeatureName = null;
        } else {
            this.serializerFeatureName = serializerFeature.name();
        }
    }


    @Override
    public <T> T convertToJavaObject(final String source, final Type targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Class<T> targetType, final Charset charset) {
        try {
            return JSON.parseObject(
                    source,
                    0,
                    source.length,
                    charset != null ? charset : StandardCharsets.UTF_8,
                    targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Type targetType, final Charset charset) {
        try {
            return JSON.parseObject(source, 0, source.length, charset, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    public <T> T convertToJavaObject(final String source, final TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }


    private String parseToString(final Object obj) {
        if (serializerFeature == null) {
            return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
        }
        return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat, serializerFeature);
    }

    @Override
    public String encodeToString(final Object obj) {
        if (obj instanceof CharSequence) {
            obj.toString();
        }
        try {
            return parseToString(obj);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
    }


    private static Object toJSON(final Object javaObject, final ForestRequest request, final ConvertOptions options) {
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        return toJSON(javaObject, parserConfig, request, options);
    }

    private static Object toJSON(final Object javaObject, final ParserConfig mapping, final ForestRequest request, final ConvertOptions options) {
        if (javaObject == null) {
            return null;
        }

        if (javaObject instanceof JSON) {
            return javaObject;
        }

        if (javaObject instanceof Map) {
            final Map<Object, Object> map = (Map<Object, Object>) javaObject;
            final JSONObject json = new JSONObject(map.size());

            for (final Map.Entry<Object, Object> entry : map.entrySet()) {
                final Object key = entry.getKey();
                final String jsonKey = TypeUtils.castToString(key);
                if (options != null && options.shouldExclude(jsonKey)) {
                    continue;
                }
                final Object jsonValue = toJSON(entry.getValue(), request, options);
                if (options != null && options.shouldIgnore(jsonValue)) {
                    continue;
                }
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (javaObject instanceof Collection) {
            final Collection<?> collection = (Collection<?>) javaObject;

            final JSONArray array = new JSONArray(collection.size());

            for (final Object item : collection) {
                final Object jsonValue = toJSON(item, request, options);
                array.add(jsonValue);
            }

            return array;
        }

        final Class<?> clazz = javaObject.getClass();

        if (clazz.isEnum()) {
            return ((Enum<?>) javaObject).name();
        }

        if (clazz.isArray()) {
            final int len = Array.getLength(javaObject);
            final JSONArray array = new JSONArray(len);

            for (int i = 0; i < len; ++i) {
                final Object item = Array.get(javaObject, i);
                final Object jsonValue = toJSON(item, request, options);
                array.add(jsonValue);
            }
            return array;
        }

        if (mapping.isPrimitive(clazz)) {
            return javaObject;
        }

        try {
            final List<FieldInfo> getters = TypeUtils.computeGetters(clazz, null);
            final JSONObject json = new JSONObject(getters.size(), true);

            for (final FieldInfo field : getters) {
                if (options != null && options.shouldExclude(field.name)) {
                    continue;
                }
                Object value = field.get(javaObject);
                if (Lazy.isEvaluatingLazyValue(value, request)) {
                    continue;
                }
                if (options != null) {
                    value = options.getValue(value, request);
                    if (options.shouldIgnore(value)) {
                        continue;
                    }
                }
                final Object jsonValue = JSON.toJSON(value);
                if (nameField != null) {
                    json.put((String) nameField.get(field), jsonValue);
                } else if (nameMethod != null) {
                    json.put((String) nameMethod.invoke(field), jsonValue);
                }
            }

            return json;
        } catch (IllegalAccessException e) {
            throw new JSONException("toJSON error", e);
        } catch (InvocationTargetException e) {
            throw new JSONException("toJSON error", e);
        }
    }



    @Override
    public Map<String, Object> convertObjectToMap(final Object obj, final ForestRequest request, final ConvertOptions options) {
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
        if (nameField == null && nameMethod == null) {
            return defaultJsonMap(obj, options);
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }
        final List<FieldInfo> getters = TypeUtils.computeGetters(obj.getClass(), null);
        final JSONObject json = new JSONObject(getters.size(), true);

        try {
            for (final FieldInfo field : getters) {
                if (options != null && options.shouldExclude(field.name)) {
                    continue;
                }
                Object value = field.get(obj);
                if (Lazy.isEvaluatingLazyValue(value, request)) {
                    continue;
                }
                if (options != null) {
                    value = options.getValue(value, request);
                    if (options.shouldIgnore(value)) {
                        continue;
                    }
                }
                if (nameField != null) {
                    json.put((String) nameField.get(field), value);
                } else if (nameMethod != null) {
                    json.put((String) nameMethod.invoke(field), value);
                }
            }
            return json;
        } catch (IllegalAccessException e) {
            return defaultJsonMap(obj, options);
        } catch (InvocationTargetException e) {
            return defaultJsonMap(obj, options);
        }
    }

    @Override
    public void setDateFormat(final String format) {
        this.dateFormat = format;
        if (StringUtils.isNotBlank(format)) {
            JSON.DEFFAULT_DATE_FORMAT = format;
        }
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
    }

    public Map<String, Object> defaultJsonMap(final Object obj, final ConvertOptions options) {
        final Object jsonObj = JSON.toJSON(obj);
        final Map<String, Object> map = (Map<String, Object>) jsonObj;
        if (map != null && options != null) {
            for (Map.Entry<String, Object> entity : map.entrySet()) {
                final String name = entity.getKey();
                if (options.shouldExclude(name)) {
                    map.remove(name);
                }
            }
        }
        return map;
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }
}
