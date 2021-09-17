package com.dtflys.forest.reflection;

import com.dtflys.forest.exceptions.ForestRuntimeException;

/**
 * 默认Forest对象工厂
 * <p>直接使用Java反射对Class进行实例化
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public class DefaultObjectFactory implements ForestObjectFactory {

    @Override
    public <T> T createInstance(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
