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
import org.dromara.forest.utils.ReflectUtils;
import org.dromara.forest.utils.StringUtils;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 使用Gson实现的消息转换实现类
 *
 * @author Gongjun
 * @since 2016-06-04
 */
public class ForestGsonConverter implements ForestJsonConverter {

    /** 日期格式 */
    private String dateFormat;

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        try {
            if (targetType instanceof ParameterizedType
                    || targetType.getClass().getName().startsWith("com.google.gson")) {
                final Gson gson = createGson();
                return gson.fromJson(source, targetType);
            }
            final Class<?> clazz = ReflectUtils.toClass(targetType);
            try {
                if (Map.class.isAssignableFrom(clazz)) {
                    final JsonParser jsonParser = new JsonParser();
                    final JsonObject jsonObject = jsonParser.parse(source).getAsJsonObject();
                    return (T) toMap(jsonObject, false);
                }
                else if (List.class.isAssignableFrom(clazz)) {
                    final JsonParser jsonParser = new JsonParser();
                    final JsonArray jsonArray = jsonParser.parse(source).getAsJsonArray();
                    return (T) toList(jsonArray);
                }
                final Gson gson = createGson();
                return (T) gson.fromJson(source, targetType);
            } catch (Throwable th) {
                throw new ForestConvertException(this, th);
            }

        } catch (Exception ex) {
            throw new ForestConvertException(this, ex);
        }
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        final String str = StringUtils.fromBytes(source, charset);
        return convertToJavaObject(str, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        final String str = StringUtils.fromBytes(source, charset);
        return convertToJavaObject(str, targetType);
    }

    private static Map<String, Object> toMap(JsonObject json, boolean singleLevel){
        final Map<String, Object> map = new HashMap<String, Object>();
        final Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Iterator<Map.Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
            final Map.Entry<String, JsonElement> entry = iter.next();
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (singleLevel) {
                if(value instanceof JsonArray) {
                    map.put(key, toList((JsonArray) value));
                }
                else if (value instanceof JsonPrimitive) {
                    map.put(key, toObject((JsonPrimitive) value));
                }
                else {
                    map.put(key, value);
                }
                continue;
            }
            if(value instanceof JsonArray) {
                map.put(key, toList((JsonArray) value));
            }
            else if(value instanceof JsonObject) {
                map.put(key, toMap((JsonObject) value, singleLevel));
            }
            else if (value instanceof JsonPrimitive) {
                map.put(key, toObject((JsonPrimitive) value));
            }
            else {
                map.put(key, value);
            }
        }
        return map;
    }

    private static Object toObject(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }
        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }
        if (jsonPrimitive.isNumber()) {
            final BigDecimal num = jsonPrimitive.getAsBigDecimal();
            final int index = num.toString().indexOf('.');
            if (index == -1) {
                if (num.compareTo(new BigDecimal(Long.MAX_VALUE)) == 1) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Long.MIN_VALUE)) == -1) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Integer.MAX_VALUE)) == 1
                        || num.compareTo(new BigDecimal(Integer.MIN_VALUE)) == -1) {
                    return jsonPrimitive.getAsLong();
                }
                return jsonPrimitive.getAsInt();
            }
            final double dvalue = jsonPrimitive.getAsDouble();
            final float fvalue = jsonPrimitive.getAsFloat();
            if (String.valueOf(dvalue).equals(fvalue)) {
                return fvalue;
            }
            return dvalue;
        }
        if (jsonPrimitive.isJsonArray()) {
            return toList(jsonPrimitive.getAsJsonArray());
        }
        if (jsonPrimitive.isJsonObject()) {
            return toMap(jsonPrimitive.getAsJsonObject(), false);
        }
        return null;
    }

    private static List<Object> toList(JsonArray json){
        final List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < json.size(); i++){
            final Object value = json.get(i);
            if (value instanceof JsonArray){
                list.add(toList((JsonArray) value));
            }
            else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value, false));
            }
            else if (value instanceof JsonPrimitive) {
                list.add(toObject((JsonPrimitive) value));
            }
            else{
                list.add(value);
            }
        }
        return list;
    }

    /**
     * 创建GSON对象
     * @return New instance of {@code com.google.gson.Gson}
     */
    private Gson createGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        if (StringUtils.isNotBlank(dateFormat)) {
            gsonBuilder.setDateFormat(dateFormat);
        }
        return gsonBuilder.create();
    }

    @Override
    public String encodeToString(Object obj) {
        final Gson gson = createGson();
        return gson.toJson(obj);
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
        final Gson gson = createGson();
        final JsonElement jsonElement = gson.toJsonTree(obj);
        return toMap(jsonElement.getAsJsonObject(), true);
    }



    public String convertToJson(Object obj, Type type) {
        final Gson gson = createGson();
        return gson.toJson(obj, type);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }

}
