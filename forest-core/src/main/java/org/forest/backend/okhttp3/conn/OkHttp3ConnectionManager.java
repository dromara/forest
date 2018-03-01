package org.forest.backend.okhttp3.conn;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.forest.backend.ForestConnectionManager;
import org.forest.http.ForestRequest;

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
        init();
    }


    public void init() {
        pool = new ConnectionPool();
    }

    public OkHttpClient getClient(ForestRequest request) {
        Integer timeout = request.getTimeout();
        if (timeout == null) {
            timeout = request.getConfiguration().getTimeout();
        }

        return new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

}
