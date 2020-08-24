package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ForestMultipartFactory<T> {

    private static Map<Class, Class> multipartTypeMap = new LinkedHashMap<>();

    private final Class<T> paramType;

    protected ForestMultipartFactory(Class<T> paramType,
                                     int index,
                                     MappingTemplate nameTemplate,
                                     MappingTemplate fileNameTemplate) {
        this.paramType = paramType;
        this.index = index;
        this.nameTemplate = nameTemplate;
        this.fileNameTemplate = fileNameTemplate;
    }

    public static <P, M> void registerFactory(Class<P> paramType, Class<M> multipartType) {
        multipartTypeMap.put(paramType, multipartType);
    }


    public static <P> ForestMultipartFactory<P> createFactory(
            Class<P> paramType,
            int index,
            MappingTemplate nameTemplate,
            MappingTemplate fileNameTemplate) {
        if (multipartTypeMap.containsKey(paramType)) {
            return new ForestMultipartFactory<>(paramType, index, nameTemplate, fileNameTemplate);
        }
        for (Class<P> pType : multipartTypeMap.keySet()) {
            if (pType.isAssignableFrom(paramType)) {
                return new ForestMultipartFactory<>(paramType, index, nameTemplate, fileNameTemplate);
            }
        }
        throw new ForestRuntimeException("[Forest] Can not wrap parameter type \"" + paramType.getName() + "\" in ForestMultipart");
    }

    static {
        registerFactory(File.class, FileMultipart.class);
        registerFactory(String.class, FilePathMultipart.class);
        registerFactory(InputStream.class, InputStreamMultipart.class);
        registerFactory(byte[].class, ByteArrayMultipart.class);
    }

    private final int index;

    private final MappingTemplate nameTemplate;

    private final MappingTemplate fileNameTemplate;


    public int getIndex() {
        return index;
    }

    public MappingTemplate getNameTemplate() {
        return nameTemplate;
    }

    public MappingTemplate getFileNameTemplate() {
        return fileNameTemplate;
    }

    public <M extends ForestMultipart<T>> M create(String name, String fileName, T data, String contentType) {
        Class<M> multipartType = multipartTypeMap.get(paramType);
        try {
            M multipart = multipartType.newInstance();
            multipart.setName(name);
            multipart.setFileName(fileName);
            multipart.setData(data);
            multipart.setContentType(contentType);
            return multipart;
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
    }


}
