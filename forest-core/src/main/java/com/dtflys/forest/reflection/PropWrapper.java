package com.dtflys.forest.reflection;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropWrapper {

    private final static Object[] GETTER_ARGS = new Object[0];

    private final String name;

    Field field;

    Method getter;

    Method setter;

    public PropWrapper(final String name) {
        this.name = name;
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

    public Object getValue(Object instance) {
        if (getter == null) {
            if (field != null) {
                try {
                    return field.get(instance);
                } catch (IllegalAccessException e) {
                    throw new ForestRuntimeException(e);
                }
            }
            throw new ForestRuntimeException("The property '" + name + "' has not getter method or accessible field");
        }
        try {
            return getter.invoke(instance, GETTER_ARGS);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public void setValue(Object instance, Object value) {
        if (setter == null) {
            if (field != null) {
                try {
                    field.set(instance, value);
                } catch (IllegalAccessException e) {
                    throw new ForestRuntimeException(e);
                }
            }
            throw new ForestRuntimeException("The property '" + name + "' has not setter method or accessible field");
        }
        try {
            setter.invoke(instance, new Object[] {value});
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
