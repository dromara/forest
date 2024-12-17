package com.dtflys.forest.utils;

import cn.hutool.core.map.MapUtil;
import com.dtflys.forest.annotation.AliasFor;
import com.dtflys.forest.annotation.BaseLifeCycle;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.OverrideAttribute;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtils {

    private static ForestJsonConverter FORM_MAP_CONVERTER;

    /**
     * 字段缓存
     */
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 方法缓存
     */
    private static final Map<Class<?>, Method[]> METHOD_CACHE = new ConcurrentHashMap<>();

    /**
     * JSON转换选择器
     * @since 1.5.0-BETA4
     */
    private static JSONConverterSelector jsonConverterSelector = new JSONConverterSelector();



    /**
     * 被排除调注解方法名集合
     */
    private static Set<String> excludedAnnotationMethodNames = new HashSet<>();
    static {
        excludedAnnotationMethodNames.add("equals");
        excludedAnnotationMethodNames.add("getClass");
        excludedAnnotationMethodNames.add("annotationType");
        excludedAnnotationMethodNames.add("notify");
        excludedAnnotationMethodNames.add("notifyAll");
        excludedAnnotationMethodNames.add("wait");
        excludedAnnotationMethodNames.add("hashCode");
        excludedAnnotationMethodNames.add("toString");
        excludedAnnotationMethodNames.add("newProxyInstance");
        excludedAnnotationMethodNames.add("newProxyClass");
        excludedAnnotationMethodNames.add("getInvocationHandler");
    }
    
    
    public static boolean isAndroid() {
        try {
            Class.forName("android.os.Build");
            return true;
        } catch (ClassNotFoundException e) {
            String osName = System.getProperty("os.name", "").toLowerCase();
            return osName.startsWith("android");
        }
    }


    /**
     * 转换为 {@link Type} 接口实例
     *
     * @param type {@link Type}接口实例
     * @return  {@link Type} 接口实例
     */
    public static Type toType(final Type type) {
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
    public static Class<?> toClass(final Type genericType) {
        if (genericType instanceof TypeReference) {
            return toClass(((TypeReference<?>) genericType).getType());
        }
        if (genericType instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) genericType;
            return ((Class<?>) pt.getRawType());
        } else if (genericType instanceof TypeVariable) {
            return (Class<?>) ((TypeVariable<?>) genericType).getBounds()[0];
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
    public static ParameterizedType toParameterizedType(final Type type) {
        if (null == type) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            return  (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (genericSuper == null || Object.class.equals(genericSuper)) {
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    genericSuper = genericInterfaces[0];
                }
            }
            return toParameterizedType(genericSuper);
        }
        return null;
    }

    /**
     * 获取所有泛型参数类型
     * <p>从一个带泛型的类型中获取其所有的泛型参数类型
     *
     * @param type {@link Type} 接口实例
     * @return 多个泛型参数类型, {@link Type} 接口数组
     */
    public static Type[] getGenericTypeArguments(final Type type) {
        final ParameterizedType pType = toParameterizedType(type);
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
    public static Type getGenericArgument(final Type type, final int index) {
        final Type[] arguments = getGenericTypeArguments(type);
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
    public static Type getGenericArgument(final Type type) {
        return getGenericArgument(type, 0);
    }

    /**
     * 是否是Java基本类型
     *
     * @param type Java类，{@link Class}类实例
     * @return {@code true}：是基本类型，{@code false}：不是基本类型
     */
    public static boolean isPrimaryType(final Class<?> type) {
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
    public static boolean isPrimaryArrayType(final Class<?> type) {
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


    private static Object invokeAnnotationMethodForAlias(
            final Annotation ann,
            final Class<? extends Annotation> clazz,
            final Method method,
            final Object[] args) {
        final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
        if (aliasFor != null) {
            final String aliasName = aliasFor.value();
            return invokeAnnotationMethod(ann, clazz, aliasName, args);
        }
        return null;
    }

    public static Map<String, Object> getAttributesFromAnnotation(final Annotation ann) {
        return getAttributesFromAnnotation(ann, false);
    }

    /**
     * 从注解对象中获取所有属性
     * @param ann 注解对象，{@link Annotation}接口实例
     * @param checkOverrideAttribute 是否检测属性重写
     * @return 注解对象中有属性 {@link Map}表对象，Key：属性名 Value：属性值
     */
    public static Map<String, Object> getAttributesFromAnnotation(final Annotation ann, final boolean checkOverrideAttribute) {
        final Map<String, Object> results = new HashMap<>();
        final Class<? extends Annotation> clazz = ann.annotationType();
        final Method[] methods = clazz.getMethods();
        final Object[] args = new Object[0];
        for (final Method method : methods) {
            final String name = method.getName();
            if (excludedAnnotationMethodNames.contains(name)) {
                continue;
            }
            if (method.getParameters().length > 0) {
                continue;
            }
            final Object ret = invokeAnnotationMethod(ann, clazz, name, args);
            final Object value = ret == null || (ret instanceof CharSequence && StringUtils.isEmpty(String.valueOf(ret))) ?
                    invokeAnnotationMethodForAlias(ann, clazz, method, args) : ret;
            if (value != null) {
                if (checkOverrideAttribute) {
                    OverrideAttribute overrideAttribute = method.getAnnotation(OverrideAttribute.class);
                    if (overrideAttribute != null) {
                        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(overrideAttribute);
                        final String attrNameFromMap = MapUtil.getStr(attrs, "name");
                        final String attrValue = StringUtils.isEmpty(attrNameFromMap) ? name : attrNameFromMap;
                        results.put(attrValue, value);
                    }
                } else {
                    results.put(name, value);
                }
            }
        }
        return results;
    }

    /**
     * 判断是否为Forest注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest注解；{@code false}: 不是Forest注解
     */
    public static boolean isForestAnnotation(final Annotation annotation) {
        return isForestParamAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest注解；{@code false}: 不是Forest注解
     */
    public static boolean isForestAnnotation(final Class<?> annotationType) {
        return isForestBaseAnnotation(annotationType)
                || isForestMethodAnnotation(annotationType)
                || isForestParamAnnotation(annotationType);
    }


    /**
     * 判断是否为Forest接口注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest接口注解；{@code false}: 不是Forest接口注解
     */
    public static boolean isForestBaseAnnotation(final Class<?> annotationType) {
        final Annotation mlcAnn = annotationType.getAnnotation(BaseLifeCycle.class);
        return mlcAnn != null;
    }


    /**
     * 判断是否为Forest方法注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest方法注解；{@code false}: 不是Forest方法注解
     */
    public static boolean isForestMethodAnnotation(final Annotation annotation) {
        return isForestMethodAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest方法注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest方法注解；{@code false}: 不是Forest方法注解
     */
    public static boolean isForestMethodAnnotation(final Class<?> annotationType) {
        final Annotation mlcAnn = annotationType.getAnnotation(MethodLifeCycle.class);
        return mlcAnn != null;
    }


    /**
     * 判断是否为Forest注解
     *
     * @param annotation 注解对象
     * @return {@code true}: 是Forest参数注解；{@code false}: 不是Forest参数注解
     */
    public static boolean isForestParamAnnotation(final Annotation annotation) {
        return isForestParamAnnotation(annotation.annotationType());
    }

    /**
     * 判断是否为Forest参数注解
     *
     * @param annotationType 注解类
     * @return {@code true}: 是Forest参数注解；{@code false}: 不是Forest参数注解
     */
    public static boolean isForestParamAnnotation(final Class<?> annotationType) {
        final Annotation mlcAnn = annotationType.getAnnotation(ParamLifeCycle.class);
        return mlcAnn != null;
    }

    public static boolean canAnnotationUseForInterface(final Class<?> annotationType) {
        return isForestBaseAnnotation(annotationType);
    }

    public static boolean canAnnotationUseForMethod(final Class<?> annotationType) {
        return isForestMethodAnnotation(annotationType);
    }

    public static boolean canAnnotationUseForParam(final Class<?> annotationType) {
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
    private static Object invokeAnnotationMethod(final Annotation ann, final Class<?> clazz, final String name, final Object[] args) {
        try {
            final Method method = clazz.getMethod(name);
            return method.invoke(ann, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
    }


    public static void copyAnnotationAttributes(Annotation source, Object target) {
        if (target == null) {
            return;
        }
        final Map<String, Object> attrs = getAttributesFromAnnotation(source);
        final Class targetClass = target.getClass();
        for (final String name : attrs.keySet()) {
            final String methodName = NameUtils.setterName(name);
            try {
                Method setterMethod = null;
                for (final Method method : targetClass.getMethods()) {
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

    /**
     * 获取类中所有的字段 (包括所有父类的)
     *
     * @param clazz 类
     * @return 字段列表
     * @since 1.5.30
     */
    public static Field[] getFields(final Class<?> clazz) {
        Validations.assertParamNotNull(clazz, "clazz");
        return FIELD_CACHE.computeIfAbsent(clazz, key -> getFieldsWithoutCache(key, true));
    }

    private static Field[] getFieldsWithoutCache(final Class<?> clazz, final boolean withSuperClassFields) {
        final List<Field> allFields = new LinkedList<>();
        Class<?> thisClass = clazz;
        while (thisClass != null && thisClass != Object.class) {
            final Field[] declaredFields = thisClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                allFields.add(declaredField);
            }
            thisClass = withSuperClassFields ? thisClass.getSuperclass() : null;
        }
        return allFields.toArray(new Field[allFields.size()]);
    }


    /**
     * 获取类中所有的方法 (包括所有父类的)
     *
     * @param clazz 类
     * @return 方法列表
     */
    public static Method[] getMethods(final Class<?> clazz) {
        Validations.assertParamNotNull(clazz, "clazz");
        return METHOD_CACHE.computeIfAbsent(clazz, key -> getMethodsWithoutCache(key, true));
    }


    private static Method[] getMethodsWithoutCache(final Class<?> clazz, final boolean withSuperClassMethods) {
        final List<Method> allMethods = new LinkedList<>();
        Class<?> thisClass = clazz;
        while (thisClass != null && thisClass != Object.class) {
            final Method[] declaredMethods = thisClass.getDeclaredMethods();
            for (final Method declaredMethod : declaredMethods) {
                allMethods.add(declaredMethod);
            }
            thisClass = withSuperClassMethods ? thisClass.getSuperclass() : null;
        }

        return allMethods.toArray(new Method[allMethods.size()]);
    }


}
