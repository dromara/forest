package com.dtflys.forest.reflection;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认Forest对象工厂
 * <p>直接使用Java反射对Class进行实例化
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public class DefaultObjectFactory implements ForestObjectFactory {

    private final Map<Class<?>, Object> forestObjectCache = new ConcurrentHashMap<>();

    /**
     * 框架中各种对象的构造方法
     */
    private Map<Class<?>, ObjectConstructor> constructorMap = new ConcurrentHashMap<>();

    /**
     * 从缓存获取Forest接口对象实例
     *
     * @param clazz Forest对象接口类
     * @param <T>   Forest对象接口类泛型
     * @return Forest对象实例
     */
    protected <T> T getObjectFromCache(Class<T> clazz) {
        return (T) forestObjectCache.get(clazz);
    }


    /**
     * 获取Forest接口对象(默认方式)
     * <p>适用于Forest相关接口(非请求客户端接口)和回调函数的工厂接口
     * <p>当这些类没有实例的情况下，会先实例化并缓存下来，以后再取会通过缓存获取对象
     * <p>实例化方式：通过JDK反射去实例化对象
     *
     * @param clazz Forest对象接口类
     * @param <T>   Forest对象接口类泛型
     * @return Forest对象实例
     */
    @Override
    public <T> T getObject(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        Object obj = getObjectFromCache(clazz);
        if (obj != null) {
            return (T) obj;
        }

        ObjectConstructor<T> constructor = constructorMap.get(clazz);
        if (constructor != null) {
            obj = constructor.construct();
            if (obj != null) {
                forestObjectCache.put(clazz, obj);
                return (T) obj;
            }
        }
        try {
            if(!clazz.isInterface()){
                obj = clazz.newInstance();
                forestObjectCache.put(clazz, obj);
                return (T) obj;
            }
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
        return null;
    }

    @Override
    public void registerConstructor(Class<?> cls, ObjectConstructor constructor) {
        constructorMap.put(cls, constructor);
    }

    @Override
    public void registerObject(Class<?> cls, Object o) {
        forestObjectCache.put(cls, o);
    }
}
