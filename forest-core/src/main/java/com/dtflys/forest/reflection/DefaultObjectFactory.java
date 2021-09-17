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

    private final static Map<Class<?>, Object> FOREST_OBJECT_CACHE = new ConcurrentHashMap<>();

    @Override
    public <T> T newInstance(Class<T> clazz) {
        if (clazz == null || clazz.isInterface()) {
            return null;
        }
        Object obj = FOREST_OBJECT_CACHE.get(clazz);
        if (obj == null) {
            try {
                obj = clazz.newInstance();
                FOREST_OBJECT_CACHE.put(clazz, obj);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return (T) obj;
    }
}
