package com.dtflys.forest.test.http.ssl;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.SSLSocketFactoryBuilder;
import com.dtflys.forest.ssl.TrustAllManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;

public class MySSLSocketFactoryBuilder implements SSLSocketFactoryBuilder {

    @Override
    public SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null,
                new TrustManager[] { new TrustAllManager() },
                new SecureRandom());
        System.out.println("do MySSLSocketFactoryBuilder");
        return sslContext.getSocketFactory();
    }
}
