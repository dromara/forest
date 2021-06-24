package com.dtflys.forest.ssl;

import com.dtflys.forest.http.ForestRequest;

import javax.net.ssl.SSLSocketFactory;

/**
 * SSLSocketFactoryBuilder
 *
 * @author LiFaXin
 * @date 2021/6/21 7:47 下午
 **/
public interface SslSocketFactoryBuilder {

    /**
     * 获取SSL Socket Factory
     *
     * @param request
     * @param protocol
     * @throws Exception
     * @return
     */
    SSLSocketFactory getSslSocketFactory(ForestRequest request, String protocol) throws Exception;
}
