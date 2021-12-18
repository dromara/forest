package com.dtflys.forest.reflection;

/**
 * 构造者接口，用于获取Forest中各种对象实例
 *
 * @author caihongming
 * @since 1.5.17
 **/
public interface ObjectConstructor<T> {

    /**
     * 返回一个新实例
     *
     * @return T
     */
    T construct();
}