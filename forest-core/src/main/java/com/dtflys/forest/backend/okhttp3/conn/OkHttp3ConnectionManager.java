package com.dtflys.forest.backend.okhttp3.conn;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttpResponseBody;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestProtocol;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.ForestX509TrustManager;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.TrustAllManager;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TimeUtils;
import okhttp3.Authenticator;
import okhttp3.ConnectionPool;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import javax.annotation.Nullable;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * 协议版本: http 1.0
     */
    private final static List<Protocol> HTTP_1_0 = Arrays.asList(Protocol.HTTP_1_0, Protocol.HTTP_1_1);

    /**
     * 协议版本: http 1.1
     */
    private final static List<Protocol> HTTP_1_1 = Arrays.asList(Protocol.HTTP_1_1);

    /**
     * 协议版本: http 2
     */
    private final static List<Protocol> HTTP_2 = Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1);

    /**
     * 协议版本映射表
     */
    private final static Map<ForestProtocol, List<Protocol>> PROTOCOL_VERSION_MAP = new HashMap<>();



    static {
        PROTOCOL_VERSION_MAP.put(ForestProtocol.HTTP_1_0, HTTP_1_0);
        PROTOCOL_VERSION_MAP.put(ForestProtocol.HTTP_1_1, HTTP_1_1);
        PROTOCOL_VERSION_MAP.put(ForestProtocol.HTTP_2, HTTP_2);
    }


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


    private List<Protocol> getProtocols(ForestRequest request) {
        ForestProtocol protocol = request.getProtocol();
        if (protocol == null) {
            protocol = ForestProtocol.HTTP_1_0;
        }
        List<Protocol> protocols = PROTOCOL_VERSION_MAP.get(protocol);
        return protocols;
    }

    private String getCacheKey(Integer connectTimeout, Integer readTimeout) {
        StringBuilder builder = new StringBuilder();
        builder.append("ok;cto=")
                .append(connectTimeout)
                .append(",rto=")
                .append(readTimeout);
        return builder.toString();
    }


    public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        Integer timeout = request.getTimeout();
        Integer connectTimeout = request.connectTimeout();
        Integer readTimeout = request.readTimeout();
        Integer writeTimeout = request.readTimeout();
        if (TimeUtils.isNone(connectTimeout)) {
            connectTimeout = timeout;
        }
        if (TimeUtils.isNone(readTimeout)) {
            readTimeout = timeout;
        }

        if (TimeUtils.isNone(writeTimeout)) {
            writeTimeout = timeout;
        }

        String key = getCacheKey(connectTimeout, readTimeout);
        OkHttpClient client = request.getRoute().getBackendClient(key);
        if (client != null && !request.isDownloadFile()) {
            return client;
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .protocols(getProtocols(request))
                .followRedirects(false)
                .followSslRedirects(false);

        // set proxy
        ForestProxy proxy = request.getProxy();
        if (proxy != null) {
            Proxy okProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
            builder.proxy(okProxy);
            if (StringUtils.isNotEmpty(proxy.getUsername())) {
                builder.proxyAuthenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(@Nullable Route route, Response response) {
                        Request.Builder proxyBuilder = response.request().newBuilder();
                        String credential = Credentials.basic(
                                proxy.getUsername(),
                                proxy.getPassword());
                        proxyBuilder.addHeader("Proxy-Authorization", credential);
                        return proxyBuilder.build();
                    }
                });
            }
        }

        if (request.isSSL()) {
            SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
            builder
                    .sslSocketFactory(sslSocketFactory, getX509TrustManager(request))
                    .hostnameVerifier(request.hostnameVerifier());
        }
        // add default interceptor
        builder.addNetworkInterceptor(chain -> {
            Response response = chain.proceed(chain.request());
            return response.newBuilder()
                    .body(new OkHttpResponseBody(request, response.body(), lifeCycleHandler))
                    .build();
        });

        client = builder.build();
        request.getRoute().cacheBackendClient(key, client);
        return client;
    }

    @Override
    public void init(ForestConfiguration configuration) {
        pool = new ConnectionPool();
    }

    /**
     * 获取OkHttp连接池对象
     *
     * @return {@link ConnectionPool}实例
     */
    public ConnectionPool getOkHttpPool() {
        return pool;
    }

}
