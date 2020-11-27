package com.dtflys.forest.multipart;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.SubVariableScope;
import com.dtflys.forest.utils.StringUtils;
import com.twitter.util.Var;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
        if (data instanceof ForestMultipart) {
            ForestMultipart multipart = (ForestMultipart) data;
            if (StringUtils.isEmpty(multipart.getName()) && StringUtils.isNotEmpty(name)) {
                multipart.setName(name);
            }
            if (StringUtils.isEmpty(multipart.getOriginalFileName()) && StringUtils.isNotEmpty(fileName)) {
                multipart.setFileName(name);
            }
            return (M) multipart;
        }
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


    public void addMultipart(MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, Object data, String contentType, List<ForestMultipart> multiparts, Object[] args) {
        if (data == null) {
            return;
        }
        VariableScope parentScope = nameTemplate.getVariableScope();
        if (data instanceof Iterable) {
            Iterable dataCollection = (Iterable) data;
            Iterator iterator = dataCollection.iterator();
            if (iterator.hasNext()) {
                return;
            }
            int i = 0;
            for (Object item : dataCollection) {
                SubVariableScope scope = new SubVariableScope(parentScope);
                scope.addVariableValue("_it", item);
                scope.addVariableValue("_index", i++);
                nameTemplate.setVariableScope(scope);
                fileNameTemplate.setVariableScope(scope);
                String name = nameTemplate.render(args);
                String fileName = fileNameTemplate.render(args);
                ForestMultipart multipart = create(name, fileName, (T) item, contentType);
                multiparts.add(multipart);
            }
        } else if (data.getClass().isArray()) {
            int len = Array.getLength(data);
            if (len == 0) {
                return;
            }
            Object firstItem = Array.get(data, 0);
            if (byte.class.isAssignableFrom(firstItem.getClass()) || firstItem instanceof Byte) {
                SubVariableScope scope = new SubVariableScope(parentScope);
                scope.addVariableValue("_it", data);
                nameTemplate.setVariableScope(scope);
                fileNameTemplate.setVariableScope(scope);

                String name = nameTemplate.render(args);
                String fileName = fileNameTemplate.render(args);
                ForestMultipart multipart = create(name, fileName, (T) data, contentType);
                multiparts.add(multipart);
                return;
            }
            for (int j = 0; j < len; j++) {
                Object item = Array.get(data, j);
                SubVariableScope scope = new SubVariableScope(parentScope);
                scope.addVariableValue("_it", item);
                scope.addVariableValue("_index", j);
                nameTemplate.setVariableScope(scope);
                fileNameTemplate.setVariableScope(scope);
                String name = nameTemplate.render(args);
                String fileName = fileNameTemplate.render(args);
                ForestMultipart multipart = create(name, fileName, (T) item, contentType);
                multiparts.add(multipart);
            }
        } else if (data instanceof Map) {
            Map map = (Map) data;
            int i = 0;
            for (Object key : map.keySet()) {
                String itemName = String.valueOf(key);
                Object item = map.get(key);
                SubVariableScope scope = new SubVariableScope(parentScope);
                scope.addVariableValue("_it", item);
                scope.addVariableValue("_key", key);
                scope.addVariableValue("_index", i++);
                fileNameTemplate.setVariableScope(scope);
                String fileName = fileNameTemplate.render(args);
                ForestMultipart multipart = create(itemName, fileName, (T) item, contentType);
                multiparts.add(multipart);
            }
        } else {
            SubVariableScope scope = new SubVariableScope(parentScope);
            scope.addVariableValue("_it", data);
            nameTemplate.setVariableScope(scope);
            fileNameTemplate.setVariableScope(scope);

            String name = nameTemplate.render(args);
            String fileName = fileNameTemplate.render(args);
            ForestMultipart multipart = create(name, fileName, (T) data, contentType);
            multiparts.add(multipart);
        }
    }

}
