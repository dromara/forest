package com.dtflys.forest.http.body;

import com.dtflys.forest.http.ForestRequestBody;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RequestBodyBuilder<T, B extends ForestRequestBody, D extends RequestBodyBuilder> {

    protected T data;

    protected String defaultValue;

    private final static Map<Class, RequestBodyBuilder> bodyBuilderMap = new LinkedHashMap<>();


    public static void registerBodyBuilder(Class clazz, RequestBodyBuilder bodyBuilder) {
        bodyBuilderMap.put(clazz, bodyBuilder);
    }

    public static RequestBodyBuilder type(Class clazz) {
        if (clazz == null) {
            return null;
        }
        RequestBodyBuilder builder = bodyBuilderMap.get(clazz);
        if (builder == null) {
            for (Class keyClass : bodyBuilderMap.keySet()) {
                if (keyClass.isAssignableFrom(clazz)) {
                    return bodyBuilderMap.get(keyClass);
                }
            }
        }
        return builder;
    }

    public D setData(T data) {
        this.data = data;
        return (D) this;
    }

    public D setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return (D) this;
    }


    public abstract B build();


    public static class StringRequestBodyBuilder extends RequestBodyBuilder<String, StringRequestBody, StringRequestBodyBuilder> {
        @Override
        public StringRequestBody build() {
            if (data == null) {
                return null;
            }
            StringRequestBody body = new StringRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class ByteArrayRequestBodyBuilder extends RequestBodyBuilder<byte[], ByteArrayRequestBody, StringRequestBodyBuilder> {
        @Override
        public ByteArrayRequestBody build() {
            if (data == null) {
                return null;
            }
            ByteArrayRequestBody body = new ByteArrayRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }


    public static class FileRequestBodyBuilder extends RequestBodyBuilder<File, FileRequestBody, StringRequestBodyBuilder> {
        @Override
        public FileRequestBody build() {
            if (data == null) {
                return null;
            }
            FileRequestBody body = new FileRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class InputStreamBodyBuilder extends RequestBodyBuilder<InputStream, InputStreamRequestBody, StringRequestBodyBuilder> {
        @Override
        public InputStreamRequestBody build() {
            if (data == null) {
                return null;
            }
            InputStreamRequestBody body = new InputStreamRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class ObjectRequestBodyBuilder extends RequestBodyBuilder<Object, ObjectRequestBody, StringRequestBodyBuilder> {
        @Override
        public ObjectRequestBody build() {
            if (data == null) {
                return null;
            }
            ObjectRequestBody body = new ObjectRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

}
