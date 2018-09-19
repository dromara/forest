package com.dtflys.forest.backend.okhttp3.conn;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.*;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 17:10
 */
public class OkHttp3ConnectionManager implements ForestConnectionManager {

    /**
     * connection pool
     */
    private ConnectionPool pool;

    public OkHttp3ConnectionManager() {
    }


    public X509TrustManager getX509TrustManager(ForestRequest request) {
        try {
            SSLKeyStore sslKeyStore = request.getKeyStore();
            if (sslKeyStore == null) {
                return new TrustAllManager();
            }
            return new ForestX509TrustManager(request.getKeyStore());
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }


    public X509TrustManager getTrustManager(KeyStore keyStore, ForestRequest request) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        X509TrustManager defaultTrustManager = (X509TrustManager) tmf
                .getTrustManagers()[0];
        return null;
    }

    public OkHttpClient getClient(ForestRequest request) {
        Integer timeout = request.getTimeout();
        if (timeout == null) {
            timeout = request.getConfiguration().getTimeout();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS);

        if ("https".equals(request.getProtocol())) {
            SSLSocketFactory sslSocketFactory = SSLUtils.getSSLSocketFactory(request);

            builder
                    .sslSocketFactory(sslSocketFactory, getX509TrustManager(request))
                    .hostnameVerifier(TrustAllHostnameVerifier.DEFAULT);
        }

        return builder.build();
    }

    @Override
    public void init(ForestConfiguration configuration) {
        pool = new ConnectionPool();
    }
}
