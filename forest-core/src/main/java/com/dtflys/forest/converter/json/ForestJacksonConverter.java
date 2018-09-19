package com.dtflys.forest.converter.json;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;


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
            throw new ForestRuntimeException(e);
        }
    }

    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return mapper.readValue(source, mapper.getTypeFactory().constructType(targetType));
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }

    }

    public <T> T convertToJavaObject(String source, Class<?> parametrized, Class<?> ...parameterClasses) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public <T> T convertToJavaObject(String source, JavaType javaType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }




    public String convertToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (Throwable e) {
            throw new ForestRuntimeException(e);
        }
    }
}
