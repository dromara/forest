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
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.utils.ForestDataType;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * 使用Fastjson实现的消息转换实现类
 * @author gongjun
 * @since 2016-05-30
 */
public class ForestFastjson2Converter implements ForestJsonConverter {

    private static final Set<Charset> SUPPORTED_CHARSETS = new HashSet<>();


    static {
        SUPPORTED_CHARSETS.add(StandardCharsets.UTF_8);
        SUPPORTED_CHARSETS.add(StandardCharsets.UTF_16);
        SUPPORTED_CHARSETS.add(StandardCharsets.UTF_16BE);
        SUPPORTED_CHARSETS.add(StandardCharsets.UTF_16LE);
        SUPPORTED_CHARSETS.add(StandardCharsets.US_ASCII);
        SUPPORTED_CHARSETS.add(StandardCharsets.ISO_8859_1);
    }


    private final List<JSONWriter.Feature> writerFeatures = new LinkedList<>();

    private final List<JSONReader.Feature> readFeatures = new LinkedList<>();

    /** 日期格式 */
    private String dateFormat;


    public List<JSONWriter.Feature> getWriterFeatures() {
        return writerFeatures;
    }

    public List<JSONReader.Feature> getReadFeatures() {
        return readFeatures;
    }

    public ForestFastjson2Converter() {
    }

    public void addWriterFeature(final JSONWriter.Feature feature) {
        this.writerFeatures.add(feature);
    }

    public void addReadFeature(final JSONReader.Feature feature) {
        this.readFeatures.add(feature);
    }

    private JSONReader.Feature[] getReadFeatureArray() {
        return readFeatures.toArray(new JSONReader.Feature[0]);
    }

    private JSONWriter.Feature[] getWriterFeatureArray() {
        return writerFeatures.toArray(new JSONWriter.Feature[0]);
    }

    @Override
    public <T> T convertToJavaObject(InputStream source, Class<T> targetType, Charset charset) {
        try {
            return JSON.parseObject(source, charset, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }


    @Override
    public <T> T convertToJavaObject(InputStream source, Type targetType, Charset charset) {
        try {
            return JSON.parseObject(source, charset, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(final String source, final Type targetType) {
        try {
            return JSON.parseObject(source, targetType, getReadFeatureArray());
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
                    targetType,
                    readFeatures.toArray(new JSONReader.Feature[0]));
        } catch (Throwable th) {
            throw new ForestConvertException(this, th);
        }
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Type targetType, final Charset charset) {
        try {
            final Charset cs = SUPPORTED_CHARSETS.contains(charset) ? charset : StandardCharsets.UTF_8;
            return JSON.parseObject(source, 0, source.length,  cs, targetType);
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
        return JSON.toJSONString(obj, dateFormat, getWriterFeatureArray());
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

    @Override
    public Map<String, Object> convertObjectToMap(final Object obj, final ForestRequest request, final ConvertOptions options) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            final Map objMap = (Map) obj;
            final Map<String, Object> newMap = new LinkedHashMap<>(objMap.size());
            for (final Object key : objMap.keySet()) {
                final String name = String.valueOf(key);
                if (options != null && options.shouldExclude(name)) {
                    continue;
                }
                final Object val = objMap.get(key);
                if (!Lazy.isEvaluatingLazyValue(val, request)) {
                    if (options != null) {
                        final Object evalValue = options.getValue(val, request);
                        if (!options.shouldIgnore(evalValue)) {
                            newMap.put(name, evalValue);
                        }
                    } else {
                        newMap.put(name, val);
                    }
                }
            }
            return newMap;
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }
        final JSONObject jsonObject = (JSONObject) Optional.ofNullable(JSON.toJSON(obj)).orElse(new JSONObject());
        final Map<String, Object> map = new LinkedHashMap<>();
        if (!jsonObject.isEmpty()) {
            for (final Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                final String name = entry.getKey();
                final Object value = entry.getValue();
                if (!Lazy.isEvaluatingLazyValue(value, request)) {
                    if (options != null) {
                        final Object evalValue = options.getValue(value, request);
                        if (!options.shouldIgnore(evalValue)) {
                            map.put(name, evalValue);
                        }
                    } else {
                        map.put(name, value);
                    }
                }
            }
        }
        return map;
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
