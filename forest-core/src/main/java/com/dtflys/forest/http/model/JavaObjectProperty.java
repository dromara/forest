package com.dtflys.forest.http.model;

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaObjectProperty implements ObjectProperty {

    private final Object instance;

    private final String name;

    private final Field field;

    private final Method getter;

    private final Method setter;

    public JavaObjectProperty(Object instance, String name, Field field, Method getter, Method setter) {
        this.instance = instance;
        this.name = name;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    @Override
    public Object getValue(ForestRequest request, ConvertOptions options) {
        if (getter != null) {
            try {
                return getter.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ForestRuntimeException(e);
            }
        }
        if (field != null) {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
        throw new ForestRuntimeException("[Forest] can not get value of field '" + name + "'");
    }




    public void setValue(Object value) {
        if (setter != null) {
            try {
                setter.invoke(instance, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ForestRuntimeException(e);
            }
        }
        if (field != null) {
            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
        throw new ForestRuntimeException("[Forest] can not set value of field '" + name + "'");
    }
}
