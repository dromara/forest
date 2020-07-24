package com.dtflys.forest.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ReflectUtil {

    /**
     * 从Type获取Class
     * @param genericType
     * @return
     */
    public static Class getClassByType(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Class clz = ((Class) pt.getRawType());
            return clz;
        } else if (genericType instanceof TypeVariable) {
            TypeVariable tType = (TypeVariable) genericType;
            String className = tType.getGenericDeclaration().toString();
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
            }
            return null;
        } else {
            Class clz = (Class) genericType;
            return clz;
        }
    }

}
