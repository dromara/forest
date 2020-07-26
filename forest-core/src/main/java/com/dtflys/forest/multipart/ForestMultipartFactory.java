package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ForestMultipartFactory<T> {

    private static Map<Class, ForestMultipartFactory> multipartFactoryMap = new HashMap<>();

    public static <P> void registerFactory(Class<P> paramType, ForestMultipartFactory<P> factory) {
        multipartFactoryMap.put(paramType, factory);
    }

    public static <P> ForestMultipartFactory<P> getFactory(Class<P> paramType) {
        ForestMultipartFactory<P> factory = multipartFactoryMap.get(paramType);
        if (factory != null) {
            return factory;
        }
        throw new ForestRuntimeException("[Forest] Can not find Multipart factory of type \"" + paramType.getName() + "\"");
    }

    static {
        registerFactory(File.class, new FileMultipartFactory());
        registerFactory(String.class, new FilePathMultipartFactory());
        registerFactory(InputStream.class, new InputStreamMultipartFactory());
        registerFactory(byte[].class, new ByteArrayMultipartFactory());
    }

    public abstract ForestMultipart create(String name, String fileName, T data, String contentType);

    public static class FileMultipartFactory extends ForestMultipartFactory<File> {

        @Override
        public ForestMultipart create(String name, String fileName, File data, String contentType) {
            return new FileMultipart(name, fileName, data, contentType);
        }
    }

    public static class FilePathMultipartFactory extends ForestMultipartFactory<String> {

        @Override
        public ForestMultipart create(String name, String fileName, String data, String contentType) {
            return new FilePathMultipart(name, fileName, data, contentType);
        }
    }

    public static class InputStreamMultipartFactory extends ForestMultipartFactory<InputStream> {

        @Override
        public ForestMultipart create(String name, String fileName, InputStream data, String contentType) {
            return new InputStreamMultipart(name, fileName, data, contentType);
        }
    }

    public static class ByteArrayMultipartFactory extends ForestMultipartFactory<byte[]> {

        @Override
        public ForestMultipart create(String name, String fileName, byte[] data, String contentType) {
            return new ByteArrayMultipart(name, fileName, data, contentType);
        }
    }

}
