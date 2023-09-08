package org.dromara.forest.ssl;


import org.dromara.forest.http.ForestRequest;

import javax.net.ssl.SSLSocketFactory;

public class SocksProxySSLSocketFactoryBuilder implements SSLSocketFactoryBuilder {


    private SSLSocketFactory socketFactory;


    @Override
    public SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception {
        return null;
    }
}
