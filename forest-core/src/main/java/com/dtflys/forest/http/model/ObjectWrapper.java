package com.dtflys.forest.http.model;

import com.dtflys.forest.utils.NameUtils;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ObjectWrapper {

    private final Object instance;

    private final List<ObjectProperty> properties = new LinkedList<>();



    private ObjectWrapper(Object instance) {
        this.instance = instance;
        init();
    }

    private void init() {
        Class<?> clazz = this.instance.getClass();
        Method[] methods = ReflectUtils.getMethods(clazz);
        for (Method method : methods) {

        }
    }

    public Object getInstance() {
        return instance;
    }

    public List<ObjectProperty> getProperties() {
        return properties;
    }

}
