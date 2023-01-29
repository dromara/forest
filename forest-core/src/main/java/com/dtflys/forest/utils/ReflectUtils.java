package com.dtflys.forest.utils;

import com.dtflys.forest.annotation.AliasFor;
import com.dtflys.forest.annotation.BaseLifeCycle;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.ParamLifeCycle;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.converter.json.JSONConverterSelector;
import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtils {

    private static ForestJsonConverter FORM_MAP_CONVERTER;

    /**
     * JSON转换选择器
     * @since 1.5.0-BETA4
     */
    private static JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();

    /**
     * 被排除调注解方法名集合
     */
    private static Set<String> excludedAnntotationMethodNames = new HashSet<>();
    static {
        excludedAnntotationMethodNames.add("equals");
        excludedAnntotationMethodNames.add("getClass");
        excludedAnntotationMethodNames.add("annotationType");
        excludedAnntotationMethodNames.add("notify");
        excludedAnntotationMethodNames.add("notifyAll");
        excludedAnntotationMethodNames.add("wait");
        excludedAnntotationMethodNames.add("hashCode");
        excludedAnntotationMethodNames.add("toString");
        excludedAnntotationMethodNames.add("newProxyInstance");
        excludedAnntotationMethodNames.add("newProxyClass");
        excludedAnntotationMethodNames.add("getInvocationHandler");
    }


    /**
     * 转换为 {@link Type} 接口实例
     *
     * @param type {@link Type}接口实例
     * @return  {@link Type} 接口实例
     */
    public static Type toType(Type type) {
        if (type instanceof TypeReference) {
            return ((TypeReference<?>) type).getType();
        }
        return type;
    }

    /**
     * 转换为 {@link Class} 类型对象
     * <p>将抽象的 {@link Type} 接口实例转换为具体的 {@link Class} 类型对象实例
     *
     * @param genericType {@link Type}接口实例
     * @return  Java类，{@link Class}类实例
     */
    public static Class<?> toClass(Type genericType) {
        if (genericType instanceof TypeReference) {
            return toClass(((TypeReference<?>) genericType).getType());
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return ((Class<?>) pt.getRawType());
        } else if (genericType instanceof TypeVariable) {
            TypeVariable<?> tType = (TypeVariable<?>) genericType;
            String className = tType.getGenericDeclaration().toString();
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ignored) {
            }
            return null;
        } else if (genericType instanceof WildcardType
                && "?".equals(genericType.toString())) {
            return Object.class;
        } else {
            try {
                return (Class<?>) genericType;
            } catch (Throwable th) {
                return Object.class;
            }
        }
    }

    /**
     * 转换为 {@link ParameterizedType} 类型对象
     * <p>将普通的 {@link Type} 类型对象转换为 {@link ParameterizedType} 类型对象
     * <p>通过 {@link ParameterizedType} 对象可以获取泛型的类型参数
     *
     * @param type 普通 Java Type 类型, {@link Type} 接口实例
     * @return 带泛型参数的 Type 类型, {@link ParameterizedType} 接口实例
     */
    public static ParameterizedType toParameterizedType(Type type) {
        if (null == type) {
            return null;
        }
        ParameterizedType pType = null;
        if (type instanceof ParameterizedType) {
            pType = (ParameterizedType) type;
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (genericSuper == null || Object.class.equals(genericSuper)) {
                Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    genericSuper = genericInterfaces[0];
                }
            }
            pType = toParameterizedType(genericSuper);
        }
        return pType;
    }

    /**
     * 获取所有泛型参数类型
     * <p>从一个带泛型的类型中获取其所有的泛型参数类型
     *
     * @param type {@link Type} 接口实例
     * @return 多个泛型参数类型, {@link Type} 接口数组
     */
    public static Type[] getGenericTypeArguments(Type type) {
        ParameterizedType pType = toParameterizedType(type);
        if (pType != null) {
            return pType.getActualTypeArguments();
        }
        return null;
    }

    /**
     * 根据下标获取单个泛型参数类型
     * <p>从一个带泛型的类型中获取其第 index 个泛型参数类型
     *
     * @param type {@link Type} 接口实例
     * @param index 泛型参数类型下标, 表示第几个泛型参数, 从0开始记
     * @return 泛型参数类型, {@link Type} 接口实例
     */
    public static Type getGenericArgument(Type type, int index) {
        Type[] arguments = getGenericTypeArguments(type);
        if (arguments != null && arguments.length > index) {
            return arguments[index];
        }
        return null;
    }

    /**
     * 获取第一个泛型参数类型
     * <p>从一个带泛型的类型中获取其第一个泛型参数类型
     *
     * @param type {@link Type} 接口实例
     * @return 泛型参数类型, {@link Type} 接口实例
     */
    public static Type getGenericArgument(Type type) {
        return getGenericArgument(type, 0);
    }

    /**
     * 是否是Java基本类型
     *
     * @param type Java类，{@link Class}类实例
     * @return {@code true}：是基本类型，{@code false}：不是基本类型
     */
    public static boolean isPrimaryType(Class<?> type) {
        if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return true;
        }
        if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return true;
        }
        if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return true;
        }
        if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return true;
        }
        if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return true;
        }
        if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return true;
        }
        if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return true;
        }
        if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            return true;
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return true;
        }
        if (BigInteger.class.isAssignableFrom(type)) {
            return true;
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }


    /**
     * 是否为基本数组类型
     * @param type Java类，{@link Class}类实例
     * @return {@code true}：是基本数组类型，{@code false}：不是基本数组类型
     */
    public static boolean isPrimaryArrayType(Class type) {
        if (!type.isArray()) {
            return false;
        }
        if (byte[].class.isAssignableFrom(type) || Byte[].class.isAssignableFrom(type)) {
            return true;
        }
        if (boolean[].class.isAssignableFrom(type) || Boolean[].class.isAssignableFrom(type)) {
            return true;
        }
        if (int[].class.isAssignableFrom(type) || Integer[].class.isAssignableFrom(type)) {
            return true;
        }
        if (long[].class.isAssignableFrom(type) || Long[].class.isAssignableFrom(type)) {
            return true;
        }
        if (short[].class.isAssignableFrom(type) || Short[].class.isAssignableFrom(type)) {
            return true;
        }
        if (float[].class.isAssignableFrom(type) || Float[].class.isAssignableFrom(type)) {
            return true;
        }
        if (double[].class.isAssignableFrom(type) || Double[].class.isAssignableFrom(type)) {
            return true;
        }
        if (BigDecimal[].class.isAssignableFrom(type)) {
            return true;
        }
        if (BigInteger[].class.isAssignableFrom(type)) {
            return true;
        }
        if (CharSequence[].class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }


    /**
     * 从注解对象中获取所有属性
     * @param ann 注解对象，{@link Annotation}接口实例
     * @return 注解对象中有属性 {@link Map}表对象，Key：属性名 Value：属性值
     */
    public static Map<String, Object> getAttributesFromAnnotation(Annotation ann) {
        Map<String, Object> results = new ConcurrentHashMap<>();
        Class clazz = ann.annotationType();
        Method[] methods = clazz.getMethods();
        Object[] args = new Object[0];
        for (Method method : methods) {
            String name = method.getName();
            if (excludedAnntotationMethodNames.contains(name)) {
                continue;
            }
            if (method.getParameters().length > 0) {
                continue;
            }
            Object value = invokeAnnotationMethod(ann, clazz, name, args);
            if (value == null ||
                    (value instanceof CharSequence && StringUtils.isEmpty(String.valueOf(value)))) {
                AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor != null) {
                    String aliasName = aliasFor.value();
                    value = invokeAnnotationMethod(ann, clazz, aliasName, args);
                }
            }
            results.put(name, value);
        }
        return results;
    }

    /**
     * 判断是否为Forest注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest注解；{@code false}: 不是Forest注解
     */
    public static boolean isForestAnnotation(Annotation annotation) {
        return isForestParamAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest注解；{@code false}: 不是Forest注解
     */
    public static boolean isForestAnnotation(Class annotationType) {
        return isForestBaseAnnotation(annotationType)
                || isForestMethodAnnotation(annotationType)
                || isForestParamAnnotation(annotationType);
    }

    /**
     * 判断是否为Forest接口注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest接口注解；{@code false}: 不是Forest接口注解
     */
    public static boolean isForestBaseAnnotation(Annotation annotation) {
        return isForestBaseAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest接口注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest接口注解；{@code false}: 不是Forest接口注解
     */
    public static boolean isForestBaseAnnotation(Class annotationType) {
        Annotation mlcAnn = annotationType.getAnnotation(BaseLifeCycle.class);
        if (mlcAnn != null) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否为Forest方法注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest方法注解；{@code false}: 不是Forest方法注解
     */
    public static boolean isForestMethodAnnotation(Annotation annotation) {
        return isForestMethodAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest方法注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest方法注解；{@code false}: 不是Forest方法注解
     */
    public static boolean isForestMethodAnnotation(Class annotationType) {
        Annotation mlcAnn = annotationType.getAnnotation(MethodLifeCycle.class);
        if (mlcAnn != null) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否为Forest注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest参数注解；{@code false}: 不是Forest参数注解
     */
    public static boolean isForestParamAnnotation(Annotation annotation) {
        return isForestParamAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest参数注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest参数注解；{@code false}: 不是Forest参数注解
     */
    public static boolean isForestParamAnnotation(Class annotationType) {
        Annotation mlcAnn = annotationType.getAnnotation(ParamLifeCycle.class);
        if (mlcAnn != null) {
            return true;
        }
        return false;
    }

    public static boolean canAnnotationUseForInterface(Class annotationType) {
        return isForestBaseAnnotation(annotationType);
    }

    public static boolean canAnnotationUseForMethod(Class annotationType) {
        return isForestMethodAnnotation(annotationType);
    }

    public static boolean canAnnotationUseForParam(Class annotationType) {
        return isForestParamAnnotation(annotationType);
    }

    /**
     * 调用注解方法
     *
     * @param ann 注解对象
     * @param clazz 注解类
     * @param name 方法名
     * @param args 方法调用参数
     * @return 方法调用返回值
     */
    private static Object invokeAnnotationMethod(Annotation ann, Class clazz, String name, Object[] args) {
        Method method = null;
        try {
            method = clazz.getMethod(name, new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new ForestRuntimeException(e);
        }
        if (method != null) {
            try {
                return method.invoke(ann, args);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return null;
    }


    public static void copyAnnotationAttributes(Annotation source, Object target) {
        if (target == null) {
            return;
        }
        Map<String, Object> attrs = getAttributesFromAnnotation(source);
        Class targetClass = target.getClass();
        for (String name : attrs.keySet()) {
            String methodName = NameUtils.setterName(name);
            try {
                Method setterMethod = null;
                for (Method method : targetClass.getMethods()) {
                    if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
                        setterMethod = method;
                        break;
                    }
                }
                if (setterMethod != null) {
                    setterMethod.invoke(target, attrs.get(name));
                }
            } catch (Throwable e) {
                throw new ForestRuntimeException(e);
            }
        }
    }


    public static Map convertObjectToMap(Object srcObj, ForestConfiguration configuration) {
        if (configuration != null) {
            return configuration.getJsonConverter().convertObjectToMap(srcObj);
        }
        if (FORM_MAP_CONVERTER == null) {
            FORM_MAP_CONVERTER = jsonConverterSelector.select();
        }
        return FORM_MAP_CONVERTER.convertObjectToMap(srcObj);
    }
}
