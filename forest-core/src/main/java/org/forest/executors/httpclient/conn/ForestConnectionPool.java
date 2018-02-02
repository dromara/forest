package org.forest.executors.httpclient.conn;

import org.apache.http.HttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-01 21:25
 */
public class ForestConnectionPool extends PoolingHttpClientConnectionManager {

    @Override
    public void connect(HttpClientConnection managedConn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {
        super.connect(managedConn, route, connectTimeout, context);
    }
}
