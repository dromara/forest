package com.dtflys.forest.reflection;

import java.util.Map;

/**
 * Forest对象工厂
 * <p>适用于Forest相关接口(非请求客户端接口)和回调函数的工厂接口
 * <p>当这些类没有实例的情况下，会先实例化并缓存下来，以后再取会通过缓存获取对象
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public interface ForestObjectFactory {

    /**
     * 获取Forest接口对象
     * <p>适用于Forest相关接口(非请求客户端接口)和回调函数的工厂接口
     * <p>当这些类没有实例的情况下，会先实例化并缓存下来，以后再取会通过缓存获取对象
     *
     * @param clazz Forest对象接口类
     * @param <T> Forest对象接口类泛型
     * @return Forest对象实例
     */
    <T> T getObject(Class<T> clazz);

    /**
     * 获取构造对象接口map
     *
     * @return ObjectConstructor
     */
    Map<Class<?>, ObjectConstructor> getConstructorMap();

    /**
     * 新增构造对象接口
     *
     * @param cls         Forest对象接口类
     * @param constructor 对象构造接口类
     */
    void putConstructor(Class<?> cls, ObjectConstructor constructor);
}
