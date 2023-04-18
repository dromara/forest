package org.dromara.forest.http.body;

import org.dromara.forest.http.ForestBodyItem;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RequestBodyBuilder<T, B extends ForestBodyItem, D extends RequestBodyBuilder> {

    private final static Map<Class, RequestBodyBuilder> bodyBuilderMap = new LinkedHashMap<>();


    public static void registerBodyBuilder(Class clazz, RequestBodyBuilder bodyBuilder) {
        bodyBuilderMap.put(clazz, bodyBuilder);
    }

    public static boolean canBuild(Class clazz) {
        RequestBodyBuilder builder = type(clazz);
        return builder != null;
    }

    public static boolean canBuild(Object obj) {
        return canBuild(obj.getClass());
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

    static {
        registerBodyBuilder(CharSequence.class, new RequestBodyBuilder.StringRequestBodyBuilder());
        registerBodyBuilder(String.class, new RequestBodyBuilder.StringRequestBodyBuilder());
        registerBodyBuilder(File.class, new RequestBodyBuilder.FileRequestBodyBuilder());
        registerBodyBuilder(byte[].class, new RequestBodyBuilder.ByteArrayRequestBodyBuilder());
        registerBodyBuilder(InputStream.class, new RequestBodyBuilder.InputStreamBodyBuilder());
        registerBodyBuilder(Object.class, new RequestBodyBuilder.ObjectRequestBodyBuilder());
    }


    public abstract B build(T data, String defaultValue);


    public static class StringRequestBodyBuilder extends RequestBodyBuilder<String, StringBodyItem, StringRequestBodyBuilder> {
        @Override
        public StringBodyItem build(String data, String defaultValue) {
            if (data == null) {
                return null;
            }
            StringBodyItem body = new StringBodyItem(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class ByteArrayRequestBodyBuilder extends RequestBodyBuilder<byte[], ByteArrayBodyItem, StringRequestBodyBuilder> {
        @Override
        public ByteArrayBodyItem build(byte[] data, String defaultValue) {
            if (data == null) {
                return null;
            }
            ByteArrayBodyItem body = new ByteArrayBodyItem(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }


    public static class FileRequestBodyBuilder extends RequestBodyBuilder<File, FileBodyItem, StringRequestBodyBuilder> {
        @Override
        public FileBodyItem build(File data, String defaultValue) {
            if (data == null) {
                return null;
            }
            FileBodyItem body = new FileBodyItem(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class InputStreamBodyBuilder extends RequestBodyBuilder<InputStream, InputStreamBodyItem, StringRequestBodyBuilder> {
        @Override
        public InputStreamBodyItem build(InputStream data, String defaultValue) {
            if (data == null) {
                return null;
            }
            InputStreamBodyItem body = new InputStreamBodyItem(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

    public static class ObjectRequestBodyBuilder extends RequestBodyBuilder<Object, ObjectBodyItem, StringRequestBodyBuilder> {
        @Override
        public ObjectBodyItem build(Object data, String defaultValue) {
            if (data == null) {
                return null;
            }
            ObjectBodyItem body = new ObjectBodyItem(data);
            body.setDefaultValue(defaultValue);
            return body;
        }
    }

}
