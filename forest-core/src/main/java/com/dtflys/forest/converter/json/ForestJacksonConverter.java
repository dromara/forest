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

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.NameUtils;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private final ThreadLocal<ForestRequest> requestThreadLocal = new InheritableThreadLocal<>();

    private final ThreadLocal<ConvertOptions> optionsThreadLocal = new InheritableThreadLocal<>();

    public ForestJacksonConverter(final ObjectMapper mapper) {
        this.mapper = mapper.copy();
    }

    public ForestJacksonConverter() {
        this.mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Lazy.class, new LazySerializer());
        this.mapper.registerModule(module);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }

    private class LazySerializer extends JsonSerializer<Lazy> {

        @Override
        public void serialize(Lazy lazy, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNull();
//            jsonGenerator.writeEndObject();
            /*ForestRequest request = (ForestRequest) serializerProvider.getAttribute("request");
            ConvertOptions options = (ConvertOptions) serializerProvider.getAttribute("options");
            if (!Lazy.isEvaluatingLazyValue(lazy, request)) {
                if (options != null) {
                    final Object evalValue = options.getValue(lazy, request);
                    if (!options.shouldIgnore(evalValue)) {
                        jsonGenerator.writeObject(evalValue);
                    }
                }
            }*/
        }
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
    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
        if (StringUtils.isNotBlank(dateFormat)) {
            final DateFormat format = new SimpleDateFormat(dateFormat);
            getMapper().setDateFormat(format);
        }
    }


    @Override
    public <T> T convertToJavaObject(final String source, final Type targetType) {
        try {
            return getMapper().readValue(source, getMapper().getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }

    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Class<T> targetType, final Charset charset) {
        try {
            final String str = StringUtils.fromBytes(source, charset);
            return getMapper().readValue(str, getMapper().getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Type targetType, final Charset charset) {
        try {
            final String str = StringUtils.fromBytes(source, charset);
            return getMapper().readValue(str, getMapper().getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public <T> T convertToJavaObject(final String source, final Class<?> parametrized, Class<?> ...parameterClasses) {
        try {
            final JavaType javaType = getMapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return getMapper().readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException(this, e);
        }
    }

    public <T> T convertToJavaObject(final String source, final JavaType javaType) {
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
    public String encodeToString(final Object obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (Throwable e) {
            throw new ForestConvertException(this, e);
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
        final ObjectMapper objectMapper = getMapper();
        final JavaType javaType = objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        final Map<String, Object> map = objectMapper.convertValue(obj, javaType);
        final Method[] methods = ReflectUtils.getMethods(obj.getClass());
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (Lazy.class.isAssignableFrom(method.getReturnType()) && method.getParameters().length == 0) {
                final String name = method.getName();
                if (!NameUtils.isGetter(name)) {
                    continue;
                }
                final String propName = NameUtils.propNameFromGetter(name);
                if (!map.containsKey(propName)) {
                    continue;
                }
                try {
                    Object val = method.invoke(obj);
                    if (val instanceof Lazy) {
                        if (Lazy.isEvaluatingLazyValue(val, request)) {
                            continue;
                        }
                        if (options != null) {
                            val = options.getValue(val, request);
                            if (options.shouldIgnore(val)) {
                                continue;
                            }
                        }
                        map.put(propName, val);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ForestRuntimeException(e);
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