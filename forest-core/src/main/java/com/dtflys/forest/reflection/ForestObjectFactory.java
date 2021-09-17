package com.dtflys.forest.reflection;

/**
 * Forest对象工厂
 * <p>用于实例化Forest相关接口(非请求客户端接口)和回调函数的工厂接口
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public interface ForestObjectFactory {

    <T> T createInstance(Class<T> clazz);
}
