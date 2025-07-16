package com.dtflys.forest.http.body;

import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.reflection.ForestVariable;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RequestBodyBuilder<T, B extends ForestRequestBody, D extends RequestBodyBuilder> {

    private final static Map<Class, RequestBodyBuilder> bodyBuilderMap = new LinkedHashMap<>();


    public static void registerBodyBuilder(Class clazz, RequestBodyBuilder bodyBuilder) {
        bodyBuilderMap.put(clazz, bodyBuilder);
    }

    public static boolean canBuild(Class clazz) {
        final RequestBodyBuilder builder = type(clazz);
        return builder != null;
    }

    public static boolean canBuild(Object obj) {
        return canBuild(obj.getClass());
    }

    public static RequestBodyBuilder type(Class clazz) {
        if (clazz == null) {
            return null;
        }
        final RequestBodyBuilder builder = bodyBuilderMap.get(clazz);
        if (builder == null) {
            for (Class keyClass : bodyBuilderMap.keySet()) {
                if (keyClass.isAssignableFrom(clazz)) {
                    return bodyBuilderMap.get(keyClass);
                }
            }
        }
        return builder;
    }

    static {
        registerBodyBuilder(CharSequence.class, new RequestBodyBuilder.StringRequestBodyBuilder());
        registerBodyBuilder(String.class, new RequestBodyBuilder.StringRequestBodyBuilder());
        registerBodyBuilder(File.class, new RequestBodyBuilder.FileRequestBodyBuilder());
        registerBodyBuilder(byte[].class, new RequestBodyBuilder.ByteArrayRequestBodyBuilder());
        registerBodyBuilder(InputStream.class, new RequestBodyBuilder.InputStreamBodyBuilder());
        registerBodyBuilder(Object.class, new RequestBodyBuilder.ObjectRequestBodyBuilder());
    }


    public abstract B build(T data, String defaultValue);


    public static class StringRequestBodyBuilder extends RequestBodyBuilder<String, StringRequestBody, StringRequestBodyBuilder> {
        @Override
        public StringRequestBody build(String data, String defaultValue) {
            if (data == null) {
                return null;
            }
            final StringRequestBody body = new StringRequestBody(ForestVariable.create(data));
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class ByteArrayRequestBodyBuilder extends RequestBodyBuilder<byte[], ByteArrayRequestBody, StringRequestBodyBuilder> {
        @Override
        public ByteArrayRequestBody build(byte[] data, String defaultValue) {
            if (data == null) {
                return null;
            }
            final ByteArrayRequestBody body = new ByteArrayRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }


    public static class FileRequestBodyBuilder extends RequestBodyBuilder<File, FileRequestBody, StringRequestBodyBuilder> {
        @Override
        public FileRequestBody build(File data, String defaultValue) {
            if (data == null) {
                return null;
            }
            final FileRequestBody body = new FileRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class InputStreamBodyBuilder extends RequestBodyBuilder<InputStream, InputStreamRequestBody, StringRequestBodyBuilder> {
        @Override
        public InputStreamRequestBody build(InputStream data, String defaultValue) {
            if (data == null) {
                return null;
            }
            final InputStreamRequestBody body = new InputStreamRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class ObjectRequestBodyBuilder extends RequestBodyBuilder<Object, ObjectRequestBody, StringRequestBodyBuilder> {
        @Override
        public ObjectRequestBody build(Object data, String defaultValue) {
            if (data == null) {
                return null;
            }
            final ObjectRequestBody body = new ObjectRequestBody(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

}
