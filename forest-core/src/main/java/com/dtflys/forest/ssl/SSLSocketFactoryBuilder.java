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
     * @param request Forest请求对象
     * @param protocol SSL协议
     * @throws Exception 可能抛出的异常类型
     * @return {@link SSLSocketFactory}实例
     */
    SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception;
}
