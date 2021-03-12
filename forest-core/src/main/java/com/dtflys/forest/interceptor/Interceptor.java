package com.dtflys.forest.interceptor;

import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestProgress;

/**
 * Forest拦截器接口
 * <p>拦截器在请求的初始化、发送请求前、发送成功、发送失败等生命周期中都会被调用</p>
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
public interface Interceptor<T> extends OnSuccess<T>, OnError, OnProgress, OnLoadCookie, OnSaveCookie {


    default void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
    }

    default boolean beforeExecute(ForestRequest request) {
        return true;
    }

    default void afterExecute(ForestRequest request, ForestResponse response) {
    }

    @Override
    default void onSuccess(T data, ForestRequest request, ForestResponse response) {
    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
    }

    @Override
    default void onProgress(ForestProgress progress) {
    }

    @Override
    default void onLoadCookie(ForestRequest request, ForestCookies cookies) {
    }

    @Override
    default void onSaveCookie(ForestRequest request, ForestCookies cookies) {
    }

    default InterceptorAttributes getAttributes(ForestRequest request) {
        return request.getInterceptorAttributes(this.getClass());
    }

    default void addAttribute(ForestRequest request, String name, Object value) {
        request.addInterceptorAttribute(this.getClass(), name, value);
    }

    default Object getAttribute(ForestRequest request, String name) {
        return request.getInterceptorAttribute(this.getClass(), name);
    }

    default <T> T getAttribute(ForestRequest request, String name, Class<T> clazz) {
        Object obj = request.getInterceptorAttribute(this.getClass(), name);
        if (obj == null) {
            return null;
        }
        return (T) obj;
    }


    default String getAttributeAsString(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return String.valueOf(attr);
    }

    default Integer getAttributeAsInteger(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return (Integer) attr;
    }

    default Float getAttributeAsFloat(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return (Float) attr;
    }

    default Double getAttributeAsDouble(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return (Double) attr;
    }

}
