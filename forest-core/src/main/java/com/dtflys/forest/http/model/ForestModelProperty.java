package com.dtflys.forest.http.model;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ForestModelProperty {

    private final Object instance;

    private final String name;

    private final Field field;

    private final Method getter;

    private final Method setter;

    public ForestModelProperty(Object instance, String name, Field field, Method getter, Method setter) {
        this.instance = instance;
        this.name = name;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
    }

    public Object getInstance() {
        return instance;
    }

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

    public Object getValue() {
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
