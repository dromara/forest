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
import okhttp3.Dispatcher;
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
     * dispatcher
     */
    private Dispatcher dispatcher;

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

    public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        Integer timeout = request.getTimeout();
        Integer connectTimeout = request.connectTimeout();
        Integer readTimeout = request.readTimeout();
        if (TimeUtils.isNone(connectTimeout)) {
            connectTimeout = timeout;
        }
        if (TimeUtils.isNone(readTimeout)) {
            readTimeout = timeout;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(pool)
                .dispatcher(dispatcher)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .protocols(getProtocols(request))
                .followRedirects(false)
                .followSslRedirects(false)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> okCookies) {
                        ForestCookies cookies = new ForestCookies();
                        for (Cookie okCookie: okCookies) {
                            long currentTime = System.currentTimeMillis();
                            ForestCookie cookie = ForestCookie.createFromOkHttpCookie(currentTime, okCookie);
                            cookies.addCookie(cookie);
                        }
                        lifeCycleHandler.handleSaveCookie(request, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        ForestCookies cookies = new ForestCookies();
                        lifeCycleHandler.handleLoadCookie(request, cookies);
                        List<ForestCookie> forestCookies = cookies.allCookies();
                        List<Cookie> okCookies = new ArrayList<>(forestCookies.size());
                        for (ForestCookie cookie : forestCookies) {
                            Duration maxAge = cookie.getMaxAge();
                            Date createTime = cookie.getCreateTime();
                            long expiresAt = createTime.getTime() + maxAge.toMillis();
                            Cookie.Builder cookieBuilder = new Cookie.Builder();
                            cookieBuilder.name(cookie.getName())
                                .value(cookie.getValue())
                                .expiresAt(expiresAt)
                                .path(cookie.getPath());
                            if (cookie.isHostOnly()) {
                                cookieBuilder.hostOnlyDomain(cookie.getDomain());
                            } else {
                                cookieBuilder.domain(cookie.getDomain());
                            }
                            if (cookie.isHttpOnly()) {
                                cookieBuilder.httpOnly();
                            }
                            if (cookie.isSecure()) {
                                cookieBuilder.secure();
                            }
                            Cookie okCookie = cookieBuilder.build();
                            okCookies.add(okCookie);
                        }
                        return okCookies;
                    }
                });

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

        return builder.build();
    }

    @Override
    public void init(ForestConfiguration configuration) {
        pool = new ConnectionPool();
        dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(configuration.getMaxConnections());
        dispatcher.setMaxRequestsPerHost(configuration.getMaxRouteConnections());
    }

    /**
     * 获取OkHttp连接池对象
     *
     * @return {@link ConnectionPool}实例
     */
    public ConnectionPool getOkHttpPool() {
        return pool;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
