package org.forest.executors.httpclient.request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.httpclient.conn.HttpclientConnectionManager;
import org.forest.http.ForestRequest;

import java.io.IOException;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-21 15:53
 */
public abstract class AbstractHttpclientRequestSender implements HttpclientRequestSender {
    private static Log log = LogFactory.getLog(AbstractHttpclientRequestSender.class);

    protected final HttpclientConnectionManager connectionManager;

    protected final ForestRequest request;

    public AbstractHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        this.connectionManager = connectionManager;
        this.request = request;
    }

    protected static void logContent(String content) {
        log.info("[Forest] " + content);
    }



}
