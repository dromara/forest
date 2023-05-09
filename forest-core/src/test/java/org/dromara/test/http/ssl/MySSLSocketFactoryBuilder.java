package org.dromara.test.http.ssl;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.ssl.SSLSocketFactoryBuilder;
import org.dromara.forest.ssl.TrustAllManager;

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
