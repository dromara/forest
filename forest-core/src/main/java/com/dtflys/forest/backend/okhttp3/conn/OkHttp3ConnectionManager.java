package com.dtflys.forest.backend.okhttp3.conn;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.SocksAuthenticator;
import com.dtflys.forest.backend.httpclient.HttpClientProvider;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.backend.okhttp3.OkHttpClientProvider;
import com.dtflys.forest.backend.okhttp3.response.OkHttpResponseBody;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestHeaderMap;
import com.dtflys.forest.http.ForestProtocol;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.ssl.ForestX509TrustManager;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.TrustAllManager;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TimeUtils;
import okhttp3.Authenticator;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 17:10
 */
public class OkHttp3ConnectionManager implements ForestConnectionManager {

    /**
     * connection pool
     */
    private volatile ConnectionPool pool;

    private volatile DefaultOkHttpClientProvider defaultOkHttpClientProvider;

    private volatile boolean inited = false;

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

    /**
     * 默认信任管理
     */
    private final static TrustAllManager DEFAULT_TRUST_MANAGER = new TrustAllManager();


    static {
        PROTOCOL_VERSION_MAP.put(ForestProtocol.HTTP_1_0, HTTP_1_0);
        PROTOCOL_VERSION_MAP.put(ForestProtocol.HTTP_1_1, HTTP_1_1);
        PROTOCOL_VERSION_MAP.put(ForestProtocol.HTTP_2, HTTP_2);
    }


    public OkHttp3ConnectionManager() {
    }

    public X509TrustManager getX509TrustManager(ForestRequest request) {
        try {
            final SSLKeyStore sslKeyStore = request.getKeyStore();
            if (sslKeyStore == null ||
                    sslKeyStore.getTrustStore() == null ||
                    sslKeyStore.getInputStream() == null) {
                return DEFAULT_TRUST_MANAGER;
            }
            return new ForestX509TrustManager(request.getKeyStore());
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }


    private List<Protocol> getProtocols(ForestRequest request) {
        final ForestProtocol protocol = Optional.ofNullable(request.getProtocol())
                .orElse(ForestProtocol.HTTP_1_0);
        return PROTOCOL_VERSION_MAP.get(protocol);
    }

    public static class DefaultOkHttpClientProvider implements OkHttpClientProvider {

        private OkHttp3ConnectionManager connectionManager;

        @Override
        public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
            if (connectionManager == null) {
                synchronized (this) {
                    if (connectionManager == null) {
                        final ForestConfiguration configuration = request.getConfiguration();
                        connectionManager = (OkHttp3ConnectionManager) configuration
                                .getBackendSelector()
                                .select(OkHttp3Backend.NAME, configuration)
                                .getConnectionManager();
                        if (!connectionManager.isInitialized()) {
                            connectionManager.init(configuration);
                        }
                    }
                }
            }
            final Integer timeout = request.getTimeout();
            final Integer connectTimeout = TimeUtils.isNone(request.connectTimeout()) ? timeout : request.connectTimeout();
            final Integer readTimeout = TimeUtils.isNone(request.readTimeout()) ? timeout : request.readTimeout();
            final Integer writeTimeout = TimeUtils.isNone(request.readTimeout()) ? timeout : request.readTimeout();

            final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectionPool(connectionManager.pool)
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(request.isSSE() ? Integer.MAX_VALUE : readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .protocols(connectionManager.getProtocols(request))
                    .followRedirects(false)
                    .followSslRedirects(false);

            // set proxy
            final ForestProxy proxy = request.getProxy();
            if (proxy != null) {
                final String username = proxy.getUsername();
                final String password = proxy.getPassword();
                final Proxy.Type type = proxy.getType() == ForestProxyType.SOCKS ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
                final Proxy okProxy = new Proxy(type, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
                builder.proxy(okProxy);
                if (type == Proxy.Type.HTTP &&
                        (StringUtils.isNotEmpty(username) || !proxy.getHeaders().isEmpty())) {
                    builder.proxyAuthenticator((route, response) -> {
                        final Request.Builder proxyBuilder = response.request().newBuilder();
                        final Charset charset = StringUtils.isNotEmpty(proxy.getCharset()) ?
                                Charset.forName(proxy.getCharset()) : null;

                        final ForestHeaderMap proxyHeaders = proxy.getHeaders();
                        if (!proxyHeaders.isEmpty()) {
                            for (Map.Entry<String, String> entry : proxyHeaders.entrySet()) {
                                proxyBuilder.addHeader(entry.getKey(), entry.getValue());
                            }
                        }
                        if (!proxyHeaders.containsKey("Proxy-Authorization")) {
                            final String credential = charset != null
                                    ? Credentials.basic(username, password, charset)
                                    : Credentials.basic(username, password);
                            proxyBuilder.addHeader("Proxy-Authorization", credential);
                        }
                        proxyBuilder.removeHeader("User-Agent");
                        proxyBuilder.removeHeader("Proxy-Connection");
                        return proxyBuilder.build();
                    });
                } else if (type == Proxy.Type.SOCKS) {
                    builder.addInterceptor(chain -> {
                        boolean hasProxyAuthenticator = false;
                        if (StringUtils.isNotEmpty(username)) {
                            SocksAuthenticator.getInstance().setPasswordAuthenticator(username, password);
                            hasProxyAuthenticator = true;
                        }
                        try {
                            return chain.proceed(chain.request());
                        } finally {
                            if (hasProxyAuthenticator) {
                                SocksAuthenticator.getInstance().removePasswordAuthenticator();
                            }
                        }
                    });
                }

            }

            if (request.isSSL()) {
                final SSLSocketFactory sslSocketFactory = request.getSSLSocketFactory();
                builder
                        .sslSocketFactory(
                                sslSocketFactory,
                                connectionManager.getX509TrustManager(request))
                        .hostnameVerifier(request.hostnameVerifier());
            }

            // add default interceptor
            if (request.getOnProgress() != null) {
                builder.addNetworkInterceptor(chain -> {
                    final Response response = chain.proceed(chain.request());
                    return response.newBuilder()
                            .body(new OkHttpResponseBody(
                                    request,
                                    response.body(),
                                    lifeCycleHandler))
                            .build();
                });
            }
            final OkHttpClient newClient = builder.build();
            return newClient;

        }
    }




    public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        final ForestConfiguration configuration = request.getConfiguration();
        final String key = "ok;" + request.clientKey();
        final boolean canCacheClient = request.cacheBackendClient() && !request.isDownloadFile();
        if (canCacheClient) {
            final OkHttpClient cachedClient = configuration.getBackendClient(key);
            if (cachedClient != null) {
                return cachedClient;
            }
        }

        OkHttpClientProvider provider = defaultOkHttpClientProvider;
        Object client = request.getBackendClient();
        if (client != null) {
            if (client instanceof OkHttpClient) {
                return (OkHttpClient) client;
            }
            if (client instanceof OkHttpClientProvider) {
                provider = (OkHttpClientProvider) client;
            } else {
                throw new ForestRuntimeException("[Forest] Backend '" +
                        request.getBackend().getName() +
                        "' does not support client of type '" +
                        client.getClass().getName() + "'");
            }
        }
        final OkHttpClient newClient = provider.getClient(request, lifeCycleHandler);
        if (canCacheClient) {
            configuration.putBackendClientToCache(key, newClient);
        }
        return newClient;
    }

    @Override
    public boolean isInitialized() {
        return inited;
    }

    @Override
    public synchronized void init(ForestConfiguration configuration) {
        if (!inited) {
            pool = new ConnectionPool();
            defaultOkHttpClientProvider = new DefaultOkHttpClientProvider();
            inited = true;
        }
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
