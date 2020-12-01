package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpConnectionConstants;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.exceptions.ForestUnsupportException;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.auth.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.lang.reflect.Proxy;
import java.nio.charset.CodingErrorAction;
import java.security.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:23
 */
public class HttpclientConnectionManager implements ForestConnectionManager {
    private HttpParams httpParams;
    private static PoolingHttpClientConnectionManager tsConnectionManager;

    private static PoolingNHttpClientConnectionManager asyncConnectionManager;

    private static Lookup<AuthSchemeProvider> authSchemeRegistry;

    private final ForestSSLConnectionFactory sslConnectFactory = new ForestSSLConnectionFactory();

    public HttpclientConnectionManager() {
//        synchronized (HttpclientConnectionManager.class) {
//            if (tsConnectionManager == null) {
//                try {
//                    init();
//                } catch (IOReactorException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    public void init(ForestConfiguration configuration) {
        try {
            httpParams = new BasicHttpParams();
            Integer maxConnections = configuration.getMaxConnections() != null ?
                    configuration.getMaxConnections() : HttpConnectionConstants.DEFAULT_MAX_TOTAL_CONNECTIONS;
            Integer maxRouteConnections = configuration.getMaxRouteConnections() != null ?
                    configuration.getMaxRouteConnections() : HttpConnectionConstants.DEFAULT_MAX_TOTAL_CONNECTIONS;

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", sslConnectFactory)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();

            tsConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            tsConnectionManager.setMaxTotal(maxConnections);
            tsConnectionManager.setDefaultMaxPerRoute(maxRouteConnections);

            /// init async connection manager
            boolean supportAsync = true;
            try {
                Class.forName("org.apache.http.nio.client.HttpAsyncClient");
            } catch (ClassNotFoundException e) {
                supportAsync = false;
            }
            if (supportAsync) {
                ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
                if (asyncConnectionManager == null) {
                    try {
                        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                                .setMalformedInputAction(CodingErrorAction.IGNORE)
                                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                                .setCharset(Consts.UTF_8).build();

                        authSchemeRegistry = RegistryBuilder
                                .<AuthSchemeProvider>create()
                                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                                .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
                                .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
                                .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
                                .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
                                .build();

                        asyncConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
                        asyncConnectionManager.setMaxTotal(maxConnections);
                        asyncConnectionManager.setDefaultMaxPerRoute(maxRouteConnections);
                        asyncConnectionManager.setDefaultConnectionConfig(connectionConfig);
                    } catch (Throwable t) {
                    }
                }
            }
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
    }

    public HttpClient getHttpClient(ForestRequest request) {
        sslConnectFactory.setCurrentRequest(request);
        HttpClientBuilder builder = HttpClients.custom();
        builder.setConnectionManager(tsConnectionManager);
        /*if ("https".equals(request.getProtocol())) {
            try {
                SSLContext sslContext = getSSLContext(request);
                SSLConnectionSocketFactory sslsf = getSSLConnectionSocketFactory(sslContext);
                builder.setSSLSocketFactory(sslsf);
            } catch (KeyManagementException e) {
                throw new ForestRuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            }
        } else {
            builder.setConnectionManager(tsConnectionManager);
        }*/

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(request.getTimeout());
        // 设置读取超时

        Integer timeout = request.getTimeout();
        if (timeout == null) {
            timeout = request.getConfiguration().getTimeout();
        }
        configBuilder.setSocketTimeout(timeout);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(HttpConnectionConstants.DEFAULT_READ_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        RequestConfig requestConfig = configBuilder.build();

        ForestProxy proxy = request.getProxy();
        if (proxy != null) {
            HttpHost httpHost = new HttpHost(proxy.getHost(), proxy.getPort());
            configBuilder.setProxy(httpHost);
        }

        return builder
                .setDefaultRequestConfig(requestConfig)
                .build();
    }


/*
    private static SSLConnectionSocketFactory getSSLConnectionSocketFactory(SSLContext sslContext) {
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return sslsf;
    }
*/


    public void afterConnect() {
        sslConnectFactory.removeCurrentRequest();
    }


//    /**
//     * 自定义SSL证书
//     * @param request
//     * @return
//     */
/*
    private static SSLContext customSSL(ForestRequest request) {
        SSLContext sslContext = null;
        SSLKeyStore keyStore = request.getKeyStore();
        final KeyStore trustStore = keyStore.getTrustStore();
        String keystorePass = keyStore.getKeystorePass();
        if (trustStore != null) {
            try {
                SSLContextBuilder scBuilder = SSLContexts.custom();
                if (keystorePass != null) {
                    sslContext = scBuilder
                            .loadKeyMaterial(trustStore, keystorePass.toCharArray())
                            .build();
                } else {
                    sslContext = scBuilder
                            .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                            .build();
                }
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            } catch (KeyManagementException e) {
                throw new ForestRuntimeException(e);
            } catch (KeyStoreException e) {
                throw new ForestRuntimeException(e);
            } catch (UnrecoverableKeyException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return sslContext;
    }
*/

//    /**
//     * 绕过SSL验证
//     *
//     * @return
//     * @throws NoSuchAlgorithmException
//     * @throws KeyManagementException
//     */
/*
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }
*/

//    /**
//     * 获取SSL上下文
//     * @param request
//     * @return
//     */
/*
    private static SSLContext getSSLContext(ForestRequest request) throws KeyManagementException, NoSuchAlgorithmException {
        if (request.getKeyStore() == null) {
            return createIgnoreVerifySSL();
        }
        return customSSL(request);
    }
*/



    public CloseableHttpAsyncClient getHttpAsyncClient(ForestRequest request) {
        if (asyncConnectionManager == null) {
            throw new ForestUnsupportException("Async forest request is unsupported.");
        }

        HttpAsyncClientBuilder builder = HttpAsyncClients.custom();
/*
        if ("https".equals(request.getProtocol())) {
            try {
                builder.setSSLContext(getSSLContext(request));
            } catch (KeyManagementException e) {
                throw new ForestRuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            }
        }
*/

        Integer timeout = request.getTimeout();
        if (timeout == null) {
            timeout = request.getConfiguration().getTimeout();
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setSocketTimeout(HttpConnectionConstants.DEFAULT_READ_TIMEOUT).build();

        return builder
                .setConnectionManager(asyncConnectionManager)
                .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

}
