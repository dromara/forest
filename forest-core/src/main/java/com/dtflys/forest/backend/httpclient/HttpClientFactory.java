package com.dtflys.forest.backend.httpclient;

import org.apache.http.client.HttpClient;

/**
 * Forest Httpclient 后端的 Client 工厂接口
 *
 * @author dt_flys[dt_flys@hotmail.com]
 * @since 1.5.23
 */
public interface HttpClientFactory {

    HttpClient getClient();

}
