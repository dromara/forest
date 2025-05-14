package com.dtflys.forest.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * Forest 拦截器接口
 * <p>相比{@link Interceptor}接口更安全，性能更好
 * 在此拦截器接口中，不再建议使用 onSuccess 回调方法来处理成功的请求响应结果。
 * 而是用 onResponse 来处理响应，并通过 response.isSuccess() 或 response.isError() 来判断请求的成功或失败
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.7.0
 */
public interface ForestInterceptor extends Interceptor<Void> {


    @Deprecated
    @Override
    default void onSuccess(Void data, ForestRequest request, ForestResponse response) {
    }
}
