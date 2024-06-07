package com.dtflys.forest.result;

import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Type;

public abstract class ResultTypeHandler<T> {

    protected final ReturnFlag flag;

    protected final Class<T> resultClass;

    public ResultTypeHandler(ReturnFlag flag, Class<T> resultClass) {
        this.flag = flag;
        this.resultClass = resultClass;
    }

    public boolean matchType(Type type) {
        final Class<?> clazz = ReflectUtils.toClass(type);
        return matchType(clazz);
    }

    public boolean matchType(Class<?> clazz) {
        return resultClass.isAssignableFrom(clazz);
    }


}
