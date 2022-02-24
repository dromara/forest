package com.dtflys.forest.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author caihongming
 * @since 1.5.20
 **/
public class MethodHandlesUtil {

    public static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;

    private static Constructor<MethodHandles.Lookup> java8LookupConstructor;

    private static Method privateLookupInMethod;

    static {
        // 先查询jdk8+ 开始提供的java.lang.invoke.MethodHandles.privateLookupIn方法
        // 如果没有说明是jdk8版本，jdk8以下的版本不考虑
        try {
            privateLookupInMethod = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupInMethod = null;
        }
        // jdk8
        if (privateLookupInMethod == null) {
            try {
                java8LookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                java8LookupConstructor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                java8LookupConstructor = null;
            }
        }
    }

    public static MethodHandles.Lookup lookup(Class<?> callerClass) {
        // 使用反射，当前版本可能不是jdk8+
        if (privateLookupInMethod != null) {
            try {
                return (MethodHandles.Lookup) privateLookupInMethod.invoke(MethodHandles.class, callerClass, MethodHandles.lookup());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return java8LookupConstructor.newInstance(callerClass, ALLOWED_MODES);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("no 'Lookup(Class, int) method in java.lang.invoke.MethodHandles.'", e);
        }
    }

    public static MethodHandle getSpecialMethodHandle(Method parentMethod) {
        final Class<?> declaringClass = parentMethod.getDeclaringClass();
        MethodHandles.Lookup lookup = lookup(declaringClass);
        try {
            return lookup.unreflectSpecial(parentMethod, declaringClass);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
