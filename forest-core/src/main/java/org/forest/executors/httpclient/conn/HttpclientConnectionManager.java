package org.forest.executors.httpclient.conn;

import org.apache.http.Consts;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.forest.config.ForestConfiguration;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.exceptions.ForestUnsupportException;
import org.forest.http.ForestRequest;
import org.forest.ssl.SSLKeyStore;
import org.forest.utils.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CodingErrorAction;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:23
 */
public class HttpclientConnectionManager {
    private HttpParams httpParams;
    private static PoolingHttpClientConnectionManager tsConnectionManager;

    private static PoolingNHttpClientConnectionManager asyncConnectionManager;

    private static Lookup<AuthSchemeProvider> authSchemeRegistry;


    /**
     * maximum number of conntections allowed
     */
    public final static int DEFAULT_MAX_TOTAL_CONNECTIONS = 500;
    /**
     * timeout in milliseconds used when retrieving
     */
    public final static int DEFAULT_TIMEOUT = 60000;
    /**
     * connect timeout
     */
    public final static int DEFAULT_CONNECT_TIMEOUT = 10000;
    /**
     * read timeout
     */
    public final static int DEFAULT_READ_TIMEOUT = 10000;

    public HttpclientConnectionManager(ForestConfiguration configuration) {
        synchronized (HttpclientConnectionManager.class) {
            if (tsConnectionManager == null) {
                try {
                    init(configuration);
                } catch (IOReactorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void init(ForestConfiguration configuration) throws IOReactorException {
        httpParams = new BasicHttpParams();
        Integer maxConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
        Integer maxRouteConnections = 10;
        Integer connectTimeout = DEFAULT_CONNECT_TIMEOUT;
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore
//                    .getDefaultType());
//            trustStore.load(null, null);
//            SSLSocketFactory sf = new ForestSSLSocketFactory(trustStore);
//            //允许所有主机的验证
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            // set maximum number of conntections allowed
//            maxConnections = configuration.getMaxConnections() != null ?
//                    configuration.getMaxConnections() : DEFAULT_MAX_TOTAL_CONNECTIONS;
//            ConnManagerParams.setMaxTotalConnections(httpParams, maxConnections);
//            // set timeout in milliseconds
//            ConnManagerParams.setTimeout(httpParams, DEFAULT_TIMEOUT);
//            // set maximum number of connections allowed per route
//            maxRouteConnections = configuration.getMaxRouteConnections() != null ?
//                    configuration.getMaxRouteConnections() : maxConnections;
//            ConnPerRouteBean connPerRoute = new ConnPerRouteBean(maxRouteConnections);
//            ConnManagerParams.setMaxConnectionsPerRoute(httpParams,connPerRoute);
//            // set connect timeout
//            connectTimeout = configuration.getConnectTimeout() != null ?
//                    configuration.getConnectTimeout() : DEFAULT_CONNECT_TIMEOUT;
//            HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
//            // set read timeout
//            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_READ_TIMEOUT);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            registry.register(new Scheme("https", sf, 443));

            tsConnectionManager = new PoolingHttpClientConnectionManager();
            tsConnectionManager.setMaxTotal(maxConnections);
            tsConnectionManager.setDefaultMaxPerRoute(maxRouteConnections);

//        } catch (IOException e) {
//            throw new ForestRuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new ForestRuntimeException(e);
//        } catch (CertificateException e) {
//            throw new ForestRuntimeException(e);
//        } catch (UnrecoverableKeyException e) {
//            throw new ForestRuntimeException(e);
//        } catch (KeyStoreException e) {
//            throw new ForestRuntimeException(e);
//        } catch (KeyManagementException e) {
//            throw new ForestRuntimeException(e);
//        }


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
                            .<AuthSchemeProvider> create()
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
    }

    public HttpClient getHttpClient(ForestRequest request) {
        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(tsConnectionManager);
        if ("https".equals(request.getProtocol())) {
            try {
                SSLContext sc = getSSLContext(request);
                SSLConnectionSocketFactory sslsf = getSSLConnectionSocketFactory(sc);
                builder.setSSLSocketFactory(sslsf);
            } catch (KeyManagementException e) {
                throw new ForestRuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            }
        }

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
        configBuilder.setConnectionRequestTimeout(DEFAULT_READ_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        RequestConfig requestConfig = configBuilder.build();

        return builder
                .setDefaultRequestConfig(requestConfig)
                .build();
//        return new DefaultHttpClient(tsConnectionManager, httpParams);
    }


    private static SSLConnectionSocketFactory getSSLConnectionSocketFactory(SSLContext sc) {
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sc,
                new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return sslsf;
    }


    /**
     * 自定义SSL证书
     * @param request
     * @return
     */
    private static SSLContext customSSL(ForestRequest request) {
        SSLContext sc = null;
        SSLKeyStore keyStore = request.getKeyStore();
        KeyStore trustStore = keyStore.getTrustStore();
        String keystorePass = keyStore.getKeystorePass();
        if (trustStore != null) {
            try {
                if (keystorePass != null) {
                    sc = SSLContexts.custom()
                            .loadKeyMaterial(trustStore, keystorePass.toCharArray())
                            .build();
                } else {
                    sc = SSLContexts.custom()
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
        return sc;
    }

    /**
     * 绕过SSL验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
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

    /**
     * 获取SSL上下文
     * @param request
     * @return
     */
    private static SSLContext getSSLContext(ForestRequest request) throws KeyManagementException, NoSuchAlgorithmException {
        if (request.getKeyStore() == null) {
            return createIgnoreVerifySSL();
        }
        return customSSL(request);
    }



    public CloseableHttpAsyncClient getHttpAsyncClient(ForestRequest request) {
        if (asyncConnectionManager == null) {
            throw new ForestUnsupportException("Async forest request is unsupported.");
        }

        HttpAsyncClientBuilder builder = HttpAsyncClients.custom();
        if ("https".equals(request.getProtocol())) {
            try {
                builder.setSSLContext(getSSLContext(request));
            } catch (KeyManagementException e) {
                throw new ForestRuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new ForestRuntimeException(e);
            }
        }

        Integer timeout = request.getTimeout();
        if (timeout == null) {
            timeout = request.getConfiguration().getTimeout();
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setSocketTimeout(DEFAULT_READ_TIMEOUT).build();

        return builder
                .setConnectionManager(asyncConnectionManager)
                .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

}
