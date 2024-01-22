package com.dtflys.forest.multipart;

import com.dtflys.forest.config.VariableScope;
import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.ForestRequestContext;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.ForestVariableContext;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ForestMultipartFactory<T> {

    private final ForestMethod<?> method;

    private static Map<Class, Class> multipartTypeMap = new LinkedHashMap<>();

    private final Class<T> paramType;

    private final String partContentType;

    protected ForestMultipartFactory(ForestMethod<?> method, Class<T> paramType,
                                     String partContentType, int index,
                                     MappingTemplate nameTemplate,
                                     MappingTemplate fileNameTemplate) {
        this.method = method;
        this.paramType = paramType;
        this.partContentType = partContentType;
        this.index = index;
        this.nameTemplate = nameTemplate;
        this.fileNameTemplate = fileNameTemplate;
    }

    public static <P, M> void registerFactory(Class<P> paramType, Class<M> multipartType) {
        multipartTypeMap.put(paramType, multipartType);
    }


    public static <P> ForestMultipartFactory<P> createFactory(
            ForestMethod<?> method,
            Class<P> paramType,
            int index,
            MappingTemplate nameTemplate,
            MappingTemplate fileNameTemplate,
            String partContentType) {
        if (multipartTypeMap.containsKey(paramType) ||
            Map.class.isAssignableFrom(paramType) ||
            Iterable.class.isAssignableFrom(paramType) ||
            paramType.isArray()) {
            return new ForestMultipartFactory<>(method, paramType, partContentType, index, nameTemplate, fileNameTemplate);
        }
        for (Class<P> pType : multipartTypeMap.keySet()) {
            if (pType.isAssignableFrom(paramType)) {
                return new ForestMultipartFactory<>(method, paramType, partContentType, index, nameTemplate, fileNameTemplate);
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
        return nameTemplate.clone();
    }

    public MappingTemplate getFileNameTemplate() {
        return fileNameTemplate.clone();
    }

    public <M extends ForestMultipart<T, ?>> M create(String name, String fileName, T data, String contentType) {
        return create(paramType, name, fileName, data, contentType);
    }

    public <M extends ForestMultipart<T, ?>> M create(Class<T> pType, String name, String fileName, T data, String contentType) {
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
        if (pType == null) {
            pType = paramType;
        }
        Class<M> multipartType = multipartTypeMap.get(pType);
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

    public void addMultipart(VariableScope parentScope, MappingTemplate nameTemplate, MappingTemplate fileNameTemplate, Object data, List<ForestMultipart> multiparts, Object[] args) {
        if (data == null) {
            return;
        }
        String contentType = partContentType;
        if (data instanceof Iterable) {
            Iterable dataCollection = (Iterable) data;
            Iterator iterator = dataCollection.iterator();
            if (!iterator.hasNext()) {
                return;
            }
            int i = 0;
            for (Object item : dataCollection) {
                ForestRequestContext context = new ForestRequestContext(parentScope, args);
                context.setVariableValue("_it", item);
                context.setVariableValue("_index", i++);
                String name = nameTemplate.render(context);
                String fileName = fileNameTemplate.render(context);
                ForestMultipart multipart = create((Class<T>) item.getClass(), name, fileName, (T) item, contentType);
                multiparts.add(multipart);
            }
        } else if (data.getClass().isArray()) {
            int len = Array.getLength(data);
            if (len == 0) {
                return;
            }
            Object firstItem = Array.get(data, 0);
            if (byte.class.isAssignableFrom(firstItem.getClass()) || firstItem instanceof Byte) {
                ForestRequestContext context = new ForestRequestContext(parentScope, args);
                context.setVariableValue("_it", data);
                String name = nameTemplate.render(context);
                String fileName = fileNameTemplate.render(context);
                ForestMultipart multipart = create(name, fileName, (T) data, contentType);
                multiparts.add(multipart);
                return;
            }
            for (int j = 0; j < len; j++) {
                Object item = Array.get(data, j);
                ForestRequestContext context = new ForestRequestContext(parentScope, args);
                context.setVariableValue("_it", item);
                context.setVariableValue("_index", j);
                String name = nameTemplate.render(context);
                String fileName = fileNameTemplate.render(context);
                ForestMultipart multipart = create((Class<T>) item.getClass(), name, fileName, (T) item, contentType);
                multiparts.add(multipart);
            }
        } else if (data instanceof Map) {
            Map map = (Map) data;
            int i = 0;
            for (Object key : map.keySet()) {
                Object item = map.get(key);
                ForestRequestContext context = new ForestRequestContext(parentScope, args);
                context.setVariableValue("_it", item);
                context.setVariableValue("_key", key);
                context.setVariableValue("_index", i++);
                String itemName = nameTemplate.render(context);
                String fileName = fileNameTemplate.render(context);
                ForestMultipart multipart = create((Class<T>) item.getClass(), itemName, fileName, (T) item, contentType);
                multiparts.add(multipart);
            }
        } else {
            ForestRequestContext context = new ForestRequestContext(parentScope, args);
            context.setVariableValue("_it", data);
            String name = nameTemplate.render(context);
            String fileName = fileNameTemplate.render(context);
            ForestMultipart multipart = create(name, fileName, (T) data, contentType);
            multiparts.add(multipart);
        }
    }

}
