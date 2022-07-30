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

import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.codec.FieldInfo;
import com.alibaba.fastjson2.util.BeanUtils;
import com.alibaba.fastjson2.util.TypeUtils;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterAdapter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;

import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * 使用Fastjson2实现的消息转换实现类
 *
 * @author lmm1990
 * @since 2016-05-30
 */
public class ForestFastjson2Converter implements ForestJsonConverter {

    /**
     * Fastjson序列化方式
     */
    private String serializerFeatureName = "";

    private JSONWriter.Feature serializerFeature;

    /**
     * 日期格式
     */
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
     *
     * @return FastJson的序列化特性名字符串
     */
    public String getSerializerFeatureName() {
        return serializerFeatureName;
    }

    /**
     * 设置FastJson的序列化特性名
     *
     * @param serializerFeatureName FastJson的序列化特性名字符串
     */
    public void setSerializerFeatureName(String serializerFeatureName) {
        if (StringUtils.isNotBlank(serializerFeatureName)) {
            this.serializerFeatureName = serializerFeatureName;
            JSONWriter.Feature feature = JSONWriter.Feature.valueOf(serializerFeatureName);
            setSerializerFeature(feature);
        }
    }

    /**
     * 获取FastJson的序列化特性对象
     *
     * @return FastJson的序列化特性对象，{@link JSONWriter.Feature}枚举实例
     */
    public JSONWriter.Feature getSerializerFeature() {
        return serializerFeature;
    }

    public ForestFastjson2Converter() {
    }

    /**
     * 设置FastJson的序列化特性对象
     *
     * @param serializerFeature FastJson的序列化特性对象，{@link JSONWriter.Feature}枚举实例
     */
    public void setSerializerFeature(JSONWriter.Feature serializerFeature) {
        this.serializerFeature = serializerFeature;
        if (serializerFeature == null) {
            this.serializerFeatureName = null;
        } else {
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
            final String text = new String(source,charset);
            return JSON.parseObject(text, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return typeReference.parseObject(source);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }

    }


    private String parseToString(Object obj) {
        if (serializerFeature == null) {
            if (this.dateFormat == null) {
                return JSON.toJSONString(obj);
            }
            return JSON.toJSONString(obj, this.dateFormat);
        }
        if (this.dateFormat == null) {
            return JSON.toJSONString(obj, serializerFeature);
        }
        return JSON.toJSONString(obj, this.dateFormat, serializerFeature);

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


    private static final Object toJSON(Object javaObject) {
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
                String jsonKey = TypeUtils.cast(key, String.class);
                Object jsonValue = toJSON(entry.getValue());
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (javaObject instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) javaObject;

            JSONArray array = new JSONArray(collection.size());

            for (Object item : collection) {
                Object jsonValue = toJSON(item);
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
                Object jsonValue = toJSON(item);
                array.add(jsonValue);
            }

            return array;
        }
        if (clazz.isPrimitive()) {
            return javaObject;
        }

        try {

            List<Method> getters = new ArrayList<>();
            BeanUtils.getters(clazz, getters::add);

            JSONObject json = new JSONObject(getters.size());

            for (Method field : getters) {
                Object value = field.invoke(javaObject);
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
        if (nameField == null && nameMethod == null) {
            return defaultJsonMap(obj);
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }
        List<Method> getters = new ArrayList<>();
        BeanUtils.getters(obj.getClass(), getters::add);

        JSONObject json = new JSONObject(getters.size());

        try {
            for (Method field : getters) {
                Object value = field.invoke(obj);
                if (nameField != null) {
                    json.put((String) nameField.get(field), value);
                } else if (nameMethod != null) {
                    json.put((String) nameMethod.invoke(field), value);
                }
            }
            return json;
        } catch (IllegalAccessException e) {
            return defaultJsonMap(obj);
        } catch (InvocationTargetException e) {
            return defaultJsonMap(obj);
        }
    }

    @Override
    public void setDateFormat(String format) {
        this.dateFormat = format;
    }

    @Override
    public String getDateFormat() {
        return this.dateFormat;
    }

    public Map<String, Object> defaultJsonMap(Object obj) {
        Object jsonObj = JSON.toJSON(obj);
        if(jsonObj instanceof String){
            return JSON.parseObject(jsonObj.toString(),new TypeReference<Map<String, Object>>(){});
        }
        return (Map<String, Object>) jsonObj;
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }
}
