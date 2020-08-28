package com.dtflys.forest.converter.json;

import com.dtflys.forest.exceptions.ForestConvertException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 使用Jackson实现的消息转折实现类
 * @author Gongjun
 * @since 2016-06-04
 */
public class ForestJacksonConverter implements ForestJsonConverter {

    private ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }

    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            return mapper.readValue(source, targetType);
        } catch (IOException e) {
            throw new ForestConvertException("json", e);
        }
    }

    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestConvertException("json", e);
        }

    }

    public <T> T convertToJavaObject(String source, Class<?> parametrized, Class<?> ...parameterClasses) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException("json", e);
        }
    }

    public <T> T convertToJavaObject(String source, JavaType javaType) {
        try {
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestConvertException("json", e);
        }
    }




    public String encodeToString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new ForestConvertException("json", e);
        }
    }

    @Override
    public Map<String, Object> convertObjectToMap(Object obj) {
        return mapper.convertValue(obj, LinkedHashMap.class);
    }
}
