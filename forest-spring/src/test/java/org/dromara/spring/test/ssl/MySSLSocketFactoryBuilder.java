package org.dromara.spring.test.ssl;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.ssl.SSLSocketFactoryBuilder;
import org.dromara.forest.ssl.TrustAllManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;

/**
 * 测试自定义Builder
 *
 * @author LiFaXin
 * @date 2021/6/21 8:08 下午
 **/
public class MySSLSocketFactoryBuilder implements SSLSocketFactoryBuilder {
    @Override
    public SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception{
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null,
                new TrustManager[] { new TrustAllManager() },
                new SecureRandom());
        System.out.println("i am custom!!! -- fx nb!");
        return sslContext.getSocketFactory();
    }
}
