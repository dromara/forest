package com.dtflys.forest.ssl;

import com.dtflys.forest.http.ForestRequest;

import javax.net.ssl.SSLSocketFactory;

public class SocksProxySSLSocketFactoryBuilder implements SSLSocketFactoryBuilder {


    private SSLSocketFactory socketFactory;


    @Override
    public SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception {
        return null;
    }
}
