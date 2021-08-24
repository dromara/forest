package com.dtflys.forest.ssl;

import com.dtflys.forest.http.ForestRequest;

import javax.net.ssl.SSLSocketFactory;

/**
 * SSLSocketFactoryBuilder
 *
 * @author LiFaXin
 * @since 2021/6/21 7:47
 **/
public interface SSLSocketFactoryBuilder {

    /**
     * 获取SSL Socket Factory
     *
     * @param request
     * @param protocol
     * @throws Exception
     * @return
     */
    SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception;
}
