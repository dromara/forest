package com.dtflys.forest.backend.okhttp3.conn;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttpResponseBody;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.ssl.ForestX509TrustManager;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLUtils;
import com.dtflys.forest.ssl.TrustAllHostnameVerifier;
import com.dtflys.forest.ssl.TrustAllManager;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.TlsVersion;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {

        Integer timeout = request.getTimeout();
        if (timeout == null) {
            timeout = request.getConfiguration().getTimeout();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> okCookies) {
                        ForestCookies cookies = new ForestCookies();
                        for (Cookie okCookie: okCookies) {
                            ForestCookie cookie = ForestCookie.createFromOkHttpCookie(okCookie);
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
        }

        if ("https".equals(request.getProtocol())) {
            String protocol = request.getSslProtocol();
            if (StringUtils.isNotBlank(protocol)) {
                if (protocol.startsWith("SSL") || protocol.startsWith("ssl")) {
                    protocol = "SSL";
                } else if (protocol.startsWith("TLS") || protocol.startsWith("TLS")) {
                    String[] strs = protocol.split("v");
                    if (strs.length == 1) {
                        protocol = "TLS";
                    } else if (strs[1] != "1.1") {
                        protocol = "TLS";
                    }
                }
            }
            SSLSocketFactory sslSocketFactory = SSLUtils.getSSLSocketFactory(request, protocol);
            builder
                    .sslSocketFactory(sslSocketFactory, getX509TrustManager(request))
                    .hostnameVerifier(TrustAllHostnameVerifier.DEFAULT);
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
    }
}
