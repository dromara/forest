package com.dtflys.forest.backend.okhttp3;

import com.dtflys.forest.http.ForestRequest;
import okhttp3.OkHttpClient;

/**
 * Forest OkHttp 后端的 Client 工厂接口
 *
 * @author dt_flys[dt_flys@hotmail.com]
 * @since 1.5.23
 */
public interface OkHttpClientFactory {

    /**
     * 获取 OkHttpClient 对象
     *
     * @return {@link OkHttpClient}对象实例
     */
    OkHttpClient getClient(ForestRequest request);

}
