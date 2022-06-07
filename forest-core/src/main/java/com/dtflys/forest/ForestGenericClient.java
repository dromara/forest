package com.dtflys.forest;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestRequest;

/**
 * Forest 通用客户端接口
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public interface ForestGenericClient {

    /**
     * 获取 Forest 通用请求
     *
     * @return {@link ForestRequest} 实例
     */
    @Request("/")
    ForestRequest<?> request();

    /**
     * 获取 Forest 通用请求
     *
     * @param clazz 带泛型参数的 {@link Class} 对象
     * @param <T> 泛型参数
     * @return {@link ForestRequest} 实例
     */
    @Request("/")
    <T> ForestRequest<T> request(Class<T> clazz);
}
