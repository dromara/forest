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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.exceptions.ForestConvertException;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.Lazy;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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
         Class clazz = FieldInfo.class;
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
    public void setSerializerFeatureName(String serializerFeatureName) {
        if (StringUtils.isNotBlank(serializerFeatureName)) {
            this.serializerFeatureName = serializerFeatureName;
            SerializerFeature feature = SerializerFeature.valueOf(serializerFeatureName);
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
    public void setSerializerFeature(SerializerFeature serializerFeature) {
        this.serializerFeature = serializerFeature;
        if (serializerFeature == null) {
            this.serializerFeatureName = null;
        }
        else {
            this.serializerFeatureName = serializerFeature.name();
        }
    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType);
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
            return JSON.parseObject(source, 0, source.length, charset, targetType);
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
        if (serializerFeature == null) {
            return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
        }
        return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat, serializerFeature);

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


    private static Object toJSON(Object javaObject, ForestRequest request, ConvertOptions options) {
        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        return toJSON(javaObject, parserConfig, request, options);
    }

    private static Object toJSON(Object javaObject, ParserConfig mapping, ForestRequest request, ConvertOptions options) {
        if (javaObject == null) {
            return null;
        }

        if (javaObject instanceof JSON) {
            return javaObject;
        }

        if (javaObject instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) javaObject;

            JSONObject json = new JSONObject(map.size());

            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object key = entry.getKey();
                String jsonKey = TypeUtils.castToString(key);
                if (options != null && options.shouldExclude(jsonKey)) {
                    continue;
                }
                Object jsonValue = toJSON(entry.getValue(), request, options);
                if (options != null && options.shouldIgnore(jsonValue)) {
                    continue;
                }
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (javaObject instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) javaObject;

            JSONArray array = new JSONArray(collection.size());

            for (Object item : collection) {
                Object jsonValue = toJSON(item, request, options);
                array.add(jsonValue);
            }

            return array;
        }

        Class<?> clazz = javaObject.getClass();

        if (clazz.isEnum()) {
            return ((Enum<?>) javaObject).name();
        }

        if (clazz.isArray()) {
            int len = Array.getLength(javaObject);

            JSONArray array = new JSONArray(len);

            for (int i = 0; i < len; ++i) {
                Object item = Array.get(javaObject, i);
                Object jsonValue = toJSON(item, request, options);
                array.add(jsonValue);
            }

            return array;
        }

        if (mapping.isPrimitive(clazz)) {
            return javaObject;
        }

        try {
            List<FieldInfo> getters = TypeUtils.computeGetters(clazz, null);

            JSONObject json = new JSONObject(getters.size(), true);

            for (FieldInfo field : getters) {
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
                Object jsonValue = JSON.toJSON(value);
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
    public Map<String, Object> convertObjectToMap(Object obj, ForestRequest request, ConvertOptions options) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            Map objMap = (Map) obj;
            Map<String, Object> newMap = new LinkedHashMap<>(objMap.size());
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
        List<FieldInfo> getters = TypeUtils.computeGetters(obj.getClass(), null);
        JSONObject json = new JSONObject(getters.size(), true);

        try {
            for (FieldInfo field : getters) {
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
    public void setDateFormat(String format) {
        this.dateFormat = format;
        if (StringUtils.isNotBlank(format)) {
            JSON.DEFFAULT_DATE_FORMAT = format;
        }
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
    }

    public Map<String, Object> defaultJsonMap(Object obj, ConvertOptions options) {
        Object jsonObj = JSON.toJSON(obj);
        Map<String, Object> map = (Map<String, Object>) jsonObj;
        if (map != null && options != null) {
            for (Map.Entry<String, Object> entity : map.entrySet()) {
                String name = entity.getKey();
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
