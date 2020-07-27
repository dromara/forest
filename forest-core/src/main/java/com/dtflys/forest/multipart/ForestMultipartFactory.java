package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class ForestMultipartFactory<T> {

    private static Map<Class, Class> multipartFactoryMap = new HashMap<>();

    protected ForestMultipartFactory(MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, MappingTemplate contentTypeTemplate) {
        this.nameTemplate = nameTemplate;
        this.fileNameTemplate = fileNameTemplate;
        this.contentTypeTemplate = contentTypeTemplate;
    }

    public static <P, R extends ForestMultipartFactory<P>> void registerFactory(Class<P> paramType, Class<R> factory) {
        multipartFactoryMap.put(paramType, factory);
    }


    public static <P, R extends ForestMultipartFactory<P>> ForestMultipartFactory<P> getFactory(
            Class<P> paramType,
            MappingTemplate nameTemplate,
            MappingTemplate fileNameTemplate,
            MappingTemplate contentTypeTemplate) {
        Class<R> factoryClass = multipartFactoryMap.get(paramType);
        if (factoryClass != null) {
            try {
                Constructor<R> constructor = factoryClass.getConstructor(MappingTemplate.class, MappingTemplate.class, MappingTemplate.class);
                R factory = constructor.newInstance(nameTemplate, fileNameTemplate, contentTypeTemplate);
                return factory;
            } catch (NoSuchMethodException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new ForestRuntimeException(e);
            }
        }
        throw new ForestRuntimeException("[Forest] Can not find Multipart factory of type \"" + paramType.getName() + "\"");
    }

    static {
        registerFactory(File.class, FileMultipartFactory.class);
        registerFactory(String.class, FilePathMultipartFactory.class);
        registerFactory(InputStream.class, InputStreamMultipartFactory.class);
        registerFactory(byte[].class, ByteArrayMultipartFactory.class);
    }

    private final MappingTemplate nameTemplate;

    private final MappingTemplate fileNameTemplate;

    private final MappingTemplate contentTypeTemplate;

    public MappingTemplate getNameTemplate() {
        return nameTemplate;
    }

    public MappingTemplate getFileNameTemplate() {
        return fileNameTemplate;
    }

    public MappingTemplate getContentTypeTemplate() {
        return contentTypeTemplate;
    }

    public abstract ForestMultipart create(String name, String fileName, T data, String contentType);

    public static class FileMultipartFactory extends ForestMultipartFactory<File> {

        public FileMultipartFactory(MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, MappingTemplate contentTypeTemplate) {
            super(nameTemplate, fileNameTemplate, contentTypeTemplate);
        }

        @Override
        public ForestMultipart create(String name, String fileName, File data, String contentType) {

            return new FileMultipart(name, fileName, data, contentType);
        }
    }

    public static class FilePathMultipartFactory extends ForestMultipartFactory<String> {

        public FilePathMultipartFactory(MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, MappingTemplate contentTypeTemplate) {
            super(nameTemplate, fileNameTemplate, contentTypeTemplate);
        }

        @Override
        public ForestMultipart create(String name, String fileName, String data, String contentType) {
            return new FilePathMultipart(name, fileName, data, contentType);
        }
    }

    public static class InputStreamMultipartFactory extends ForestMultipartFactory<InputStream> {

        public InputStreamMultipartFactory(MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, MappingTemplate contentTypeTemplate) {
            super(nameTemplate, fileNameTemplate, contentTypeTemplate);
        }

        @Override
        public ForestMultipart create(String name, String fileName, InputStream data, String contentType) {
            return new InputStreamMultipart(name, fileName, data, contentType);
        }
    }

    public static class ByteArrayMultipartFactory extends ForestMultipartFactory<byte[]> {

        public ByteArrayMultipartFactory(MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, MappingTemplate contentTypeTemplate) {
            super(nameTemplate, fileNameTemplate, contentTypeTemplate);
        }

        @Override
        public ForestMultipart create(String name, String fileName, byte[] data, String contentType) {
            return new ByteArrayMultipart(name, fileName, data, contentType);
        }
    }

}
