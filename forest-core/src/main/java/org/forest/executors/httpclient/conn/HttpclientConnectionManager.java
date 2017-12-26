package org.forest.executors.httpclient.conn;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
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

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:23
 */
public class HttpclientConnectionManager {
    private HttpParams httpParams;
    private static ThreadSafeClientConnManager tsConnectionManager;

    private static PoolingNHttpClientConnectionManager asyncConnectionManager;

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
        Integer maxConnections = null;

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            //允许所有主机的验证
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // set maximum number of conntections allowed
            maxConnections = configuration.getMaxConnections() != null ?
                    configuration.getMaxConnections() : DEFAULT_MAX_TOTAL_CONNECTIONS;
            ConnManagerParams.setMaxTotalConnections(httpParams, maxConnections);
            // set timeout in milliseconds
            ConnManagerParams.setTimeout(httpParams, DEFAULT_TIMEOUT);
            // set maximum number of connections allowed per route
            Integer maxRouteConnections = configuration.getMaxRouteConnections() != null ?
                    configuration.getMaxRouteConnections() : maxConnections;
            ConnPerRouteBean connPerRoute = new ConnPerRouteBean(maxRouteConnections);
            ConnManagerParams.setMaxConnectionsPerRoute(httpParams,connPerRoute);
            // set connect timeout
            Integer connectTimeout = configuration.getConnectTimeout() != null ?
                    configuration.getConnectTimeout() : DEFAULT_CONNECT_TIMEOUT;
            HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout);
            // set read timeout
            HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_READ_TIMEOUT);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            tsConnectionManager = new ThreadSafeClientConnManager(httpParams, registry);

        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new ForestRuntimeException(e);
        } catch (CertificateException e) {
            throw new ForestRuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new ForestRuntimeException(e);
        } catch (KeyStoreException e) {
            throw new ForestRuntimeException(e);
        } catch (KeyManagementException e) {
            throw new ForestRuntimeException(e);
        }


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
                    asyncConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
                    asyncConnectionManager.setMaxTotal(maxConnections);
                } catch (Throwable t) {
                }
            }
        }
    }

    public HttpClient getHttpClient() {
        return new DefaultHttpClient(tsConnectionManager, httpParams);
    }


    public CloseableHttpAsyncClient getHttpAsyncClient() {
        if (asyncConnectionManager == null) {
            throw new ForestUnsupportException("Async forest request is unsupported.");
        }
        return HttpAsyncClients.custom().setConnectionManager(asyncConnectionManager).build();
    }

}
