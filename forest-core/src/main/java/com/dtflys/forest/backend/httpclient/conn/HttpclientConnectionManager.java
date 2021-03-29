package com.dtflys.forest.backend.httpclient.conn;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpConnectionConstants;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.exceptions.ForestUnsupportException;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
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
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
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

    public HttpClient getHttpClient(ForestRequest request, CookieStore cookieStore) {
        sslConnectFactory.setCurrentRequest(request);
        HttpClientBuilder builder = HttpClients.custom();
        builder.setConnectionManager(tsConnectionManager);

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
        // 设置Cookie策略
        configBuilder.setCookieSpec(CookieSpecs.STANDARD);

        ForestProxy forestProxy = request.getProxy();
        if (forestProxy != null) {
            HttpHost proxy = new HttpHost(forestProxy.getHost(), forestProxy.getPort());
            if (StringUtils.isNotEmpty(forestProxy.getUsername()) &&
                    StringUtils.isNotEmpty(forestProxy.getPassword())) {
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(
                        new AuthScope(proxy),
                        new UsernamePasswordCredentials(
                                forestProxy.getUsername(),
                                forestProxy.getPassword()));
                builder.setDefaultCredentialsProvider(provider);
            }
            configBuilder.setProxy(proxy);

        }
        if (cookieStore != null) {
            builder.setDefaultCookieStore(cookieStore);
        }


        RequestConfig requestConfig = configBuilder.build();
        HttpClient httpClient = builder
                .setDefaultRequestConfig(requestConfig)
                .build();

        return httpClient;
    }



    public void afterConnect() {
        sslConnectFactory.removeCurrentRequest();
    }



    public CloseableHttpAsyncClient getHttpAsyncClient(ForestRequest request) {
        if (asyncConnectionManager == null) {
            throw new ForestUnsupportException("Async forest request is unsupported.");
        }

        HttpAsyncClientBuilder builder = HttpAsyncClients.custom();

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
